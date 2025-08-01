package sistema.custom;

import javax.swing.Icon;
import javax.swing.JButton;
import pagination.DefaultPaginationItemRender;

public class CustomPaginationItemRender extends DefaultPaginationItemRender {

    @Override
    public Object createPreviousIcon() {
        return "Anterior";
    }

    @Override
    public Object createNextIcon() {
        return "Siguiente";
    }
}