package hello;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.MenuBar.*;

@SpringUI
@Theme("valo")
public class VaadinUI extends UI {

    private static final Logger log = LoggerFactory.getLogger(VaadinUI.class);

    private final CustomerRepository repo;

    private final CustomerEditor editor;

    final Grid grid;

    final TextField filter;

    private final Button addNewBtn;

    @Autowired
    public VaadinUI(CustomerRepository repo, CustomerEditor editor) {
        this.repo = repo;
        this.editor = editor;
        this.grid = new Grid();
        this.filter = new TextField();
        this.addNewBtn = new Button("New customer", FontAwesome.PLUS);
    }

    @Override
    protected void init(VaadinRequest request) {

        VaadinSession session = getSession();

        if (session.getAttribute("auth") == null || session.getAttribute("auth").equals(Boolean.FALSE)) {
            LoginForm loginForm = new LoginForm();
            VerticalLayout loginLayout = new VerticalLayout(loginForm);
            loginLayout.setHeight(100, Unit.PERCENTAGE);
            loginLayout.setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);
            setContent(loginLayout);

            loginForm.addLoginListener((e) -> {
                if (e.getLoginParameter("username").equals("root"))
                    session.setAttribute("auth", true);
                this.init(request);
            });
        }

        // build layout

        else {
            HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
            VerticalLayout mainLayout = new VerticalLayout(actions, grid, editor);
            setContent(mainLayout);
            // Configure layouts and components
            actions.setSpacing(true);
            mainLayout.setMargin(true);
            mainLayout.setSpacing(true);

            grid.setWidth(100, Unit.PERCENTAGE);
            grid.setHeight(300, Unit.PIXELS);
            grid.setColumns("id", "firstName", "lastName");

            filter.setInputPrompt("Filter by last name");

            // Hook logic to components

            // Replace listing with filtered content when user changes filter
            filter.addTextChangeListener(e -> listCustomers(e.getText()));

            // Connect selected Customer to editor or hide if none is selected
            grid.addSelectionListener(e -> {
                if (e.getSelected().isEmpty()) {
                    editor.setVisible(false);
                } else {
                    editor.editCustomer((Customer) grid.getSelectedRow());
                }
            });

            // Instantiate and edit new Customer the new button is clicked
            addNewBtn.addClickListener(e -> editor.editCustomer(new Customer("", "")));

            // Listen changes made by the editor, refresh data from backend
            editor.setChangeHandler(() -> {
                editor.setVisible(false);
                listCustomers(filter.getValue());
            });

            // Initialize listing
            listCustomers(null);
        }


        MenuBar barmenu = new MenuBar();
        ((Layout) getContent()).addComponent(barmenu);
        ((VerticalLayout) getContent()).setComponentAlignment(barmenu, Alignment.TOP_LEFT);

        MenuItem drinks = barmenu.addItem("Beverages", null, null);
        final Label selection = new Label("-");
        ((Layout) getContent()).addComponent(selection);
        MenuBar.Command mycommand = new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                selection.setValue("Ordered a " +
                        selectedItem.getText() +
                        " from menu.");
            }
        };
// Submenu item with a sub-submenu
        MenuItem hots = drinks.addItem("Hot", null, null);
        hots.addItem("Tea",
                new ThemeResource("icons/tea-16px.png"), mycommand);
        hots.addItem("Coffee",
                new ThemeResource("icons/coffee-16px.png"), mycommand);

// Another submenu item with a sub-submenu
        MenuItem colds = drinks.addItem("Cold", null, null);
        colds.addItem("Milk", null, mycommand);
        colds.addItem("Weissbier", null, mycommand);

// Another top-level item
        MenuItem snacks = barmenu.addItem("Snacks", null, null);
        snacks.addItem("Weisswurst", null, mycommand);
        snacks.addItem("Bratwurst", null, mycommand);
        snacks.addItem("Currywurst", null, mycommand);

// Yet another top-level item
        MenuItem servs = barmenu.addItem("Services", null, null);
        servs.addItem("Car Service", null, mycommand);
    }

    // tag::listCustomers[]
    void listCustomers(String text) {
        if (StringUtils.isEmpty(text)) {
            grid.setContainerDataSource(
                    new BeanItemContainer(Customer.class, repo.findAll()));
        } else {
            grid.setContainerDataSource(new BeanItemContainer(Customer.class,
                    repo.findByFirstNameIgnoreCaseContaining(text)));
        }
    }
    // end::listCustomers[]

}