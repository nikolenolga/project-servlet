package com.tictactoe;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name="LogicServlet", urlPatterns = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession currentSession = req.getSession();
        Field field = extractField(currentSession);

        //получаем значение ячейки по которой кликнули
        int index = getSelectedIndex(req);
        Sign currentSign = field.getField().get(index);

        //если ячейка не пуста или уже есть победитель - игра закончена возвращаемся на страницу
        if(Sign.EMPTY != currentSign || currentSession.getAttribute("winner") != null) {
            resp.sendRedirect("/index.jsp");
            return;
        }

        //ход крестиком - ход, проверка на победу
        field.getField().put(index, Sign.CROSS);
        //если уже есть победитель - игра закончена возвращаемся на страницу
        if(checkWin(resp, currentSession, field)) {
            return;
        }

        //ход ноликом - проверка на наличие пустых ячеек, ход, проверка на победу
        int emptyFieldIndex = field.getSmartMoveIndex();

        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            //если уже есть победитель - игра закончена возвращаемся на страницу
            if(checkWin(resp, currentSession, field)) {
                return;
            }
        } else {
            //если ячейки закончились - устанавливаем в победителя EMPTY как индикатор ничьи
            currentSession.setAttribute("winner", Sign.EMPTY);
        }


        //пишем изменения в сессию и возвращаемся на страницу
        List<Sign> data = field.getFieldData();

        currentSession.setAttribute("data", data);
        currentSession.setAttribute("field", field);

        resp.sendRedirect("/index.jsp");
    }

    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    public Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if(fieldAttribute.getClass() != Field.class) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }

    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {

            //если победитель найден обновляем данные и пишем новый атрибуд победителя и сохраняем
            currentSession.setAttribute("winner", winner);
            List<Sign> data = field.getFieldData();
            currentSession.setAttribute("data", data);

            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
