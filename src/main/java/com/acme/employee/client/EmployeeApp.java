package com.acme.employee.client;

import com.acme.employee.shared.EmployeeDTO;
import com.acme.employee.shared.EmployeeService;
import com.acme.employee.shared.EmployeeServiceAsync;
import com.acme.employee.shared.ServiceException;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

import java.util.ArrayList;
import java.util.List;

public class EmployeeApp implements EntryPoint {

    private static final int REFRESH_MS = 15000;
    private static final NumberFormat MONEY = NumberFormat.getFormat("#,##0.00");

    private final EmployeeServiceAsync service = GWT.create(EmployeeService.class);

    private final TextBox idBox = new TextBox();
    private final TextBox nameBox = new TextBox();
    private final TextBox salaryBox = new TextBox();
    private final Button submitBtn = new Button("Submit");
    private final Button clearBtn = new Button("Clear");
    private final Label status = new Label();
    private final Label formTitle = new Label("Add designation");

    private final ListDataProvider<EmployeeDTO> provider = new ListDataProvider<EmployeeDTO>(new ProvidesKey<EmployeeDTO>() {
        @Override public Object getKey(EmployeeDTO item) { return item == null ? null : item.getId(); }
    });
    private final DataGrid<EmployeeDTO> grid = new DataGrid<EmployeeDTO>(500);
    private final List<Anchor> navLinks = new ArrayList<Anchor>();
    private boolean editing = false;

    @Override
    public void onModuleLoad() {
        FlowPanel root = new FlowPanel();
        root.add(buildHeader());

        HorizontalPanel layout = new HorizontalPanel();
        layout.setStyleName("layout");
        VerticalPanel left = buildFormPanel();
        VerticalPanel right = buildGridPanel();
        layout.add(left);
        layout.add(right);
        layout.setCellWidth(left, "340px");
        layout.setCellWidth(right, "100%");

        FlowPanel content = new FlowPanel();
        content.setStyleName("content");
        content.add(layout);
        root.add(content);

        RootPanel.get("app").add(root);

        loadData(true);
        new Timer() {                       // near real-time: poll so other users' changes appear
            @Override public void run() { loadData(false); }
        }.scheduleRepeating(REFRESH_MS);
    }

    // ---------- UI construction ----------

    private FlowPanel buildHeader() {
        FlowPanel bar = new FlowPanel();
        bar.setStyleName("topbar");
        Label brand = new Label("Employee Management");
        brand.setStyleName("brand");
        bar.add(brand);

        FlowPanel nav = new FlowPanel();
        nav.setStyleName("nav");
        for (final String text : new String[]{"Home", "Assign Department", "Assign Project", "Assign Designation", "Attendance"}) {
            final Anchor a = new Anchor(text);
            a.addClickHandler(new ClickHandler() {
                @Override public void onClick(ClickEvent event) {
                    for (Anchor l : navLinks) l.removeStyleName("active");
                    a.addStyleName("active");
                    if ("Home".equals(text) || "Assign Designation".equals(text)) {
                        showStatus("", "status-info");
                    } else {
                        showStatus("\"" + text + "\" is not available yet.", "status-info");
                    }
                }
            });
            if ("Home".equals(text)) a.addStyleName("active");
            navLinks.add(a);
            nav.add(a);
        }
        bar.add(nav);
        return bar;
    }

    private VerticalPanel buildFormPanel() {
        VerticalPanel p = new VerticalPanel();
        p.setStyleName("card");
        p.setWidth("100%");
        formTitle.getElement().getStyle().setProperty("fontSize", "16px");
        formTitle.getElement().getStyle().setProperty("fontWeight", "600");
        p.add(formTitle);

        idBox.getElement().setPropertyString("placeholder", "Auto-generated if empty");
        nameBox.getElement().setPropertyString("placeholder", "e.g. Senior Developer");
        salaryBox.getElement().setPropertyString("placeholder", "0.00");
        nameBox.setMaxLength(100);
        idBox.setStyleName("input");
        nameBox.setStyleName("input");
        salaryBox.setStyleName("input");

        p.add(labelled("Employee / Designation ID", idBox));
        p.add(labelled("Name", nameBox));
        p.add(labelled("Salary per hour", salaryBox));

        submitBtn.setStyleName("btn btn-primary");
        clearBtn.setStyleName("btn");
        submitBtn.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent e) { onSubmit(); }
        });
        clearBtn.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent e) { resetForm(); showStatus("", "status-info"); }
        });
        FlowPanel actions = new FlowPanel();
        actions.setStyleName("form-actions");
        actions.add(submitBtn);
        actions.add(clearBtn);
        p.add(actions);

        status.setStyleName("status");
        p.add(status);
        return p;
    }

    private FlowPanel labelled(String text, TextBox box) {
        FlowPanel f = new FlowPanel();
        Label l = new Label(text);
        l.setStyleName("field-label");
        box.setWidth("100%");
        f.add(l);
        f.add(box);
        return f;
    }

    private VerticalPanel buildGridPanel() {
        VerticalPanel p = new VerticalPanel();
        p.setWidth("100%");

        TextColumn<EmployeeDTO> idCol = new TextColumn<EmployeeDTO>() {
            @Override public String getValue(EmployeeDTO o) { return String.valueOf(o.getId()); }
        };
        TextColumn<EmployeeDTO> nameCol = new TextColumn<EmployeeDTO>() {
            @Override public String getValue(EmployeeDTO o) { return o.getName(); }
        };
        TextColumn<EmployeeDTO> salaryCol = new TextColumn<EmployeeDTO>() {
            @Override public String getValue(EmployeeDTO o) { return MONEY.format(o.getSalaryPerHour()); }
        };
        Column<EmployeeDTO, EmployeeDTO> actionCol = new Column<EmployeeDTO, EmployeeDTO>(new ActionsCell()) {
            @Override public EmployeeDTO getValue(EmployeeDTO o) { return o; }
        };

        grid.addColumn(idCol, "Des id");
        grid.addColumn(nameCol, "Des name");
        grid.addColumn(salaryCol, "salary/hr");
        grid.addColumn(actionCol, "Actions");
        grid.setColumnWidth(idCol, "90px");
        grid.setColumnWidth(salaryCol, "130px");
        grid.setColumnWidth(actionCol, "160px");
        grid.setHeight("480px");
        grid.setWidth("100%");
        grid.setEmptyTableWidget(emptyNote());
        provider.addDataDisplay(grid);

        FlowPanel wrap = new FlowPanel();
        wrap.setStyleName("employee-grid");
        wrap.add(grid);
        p.add(wrap);
        return p;
    }

    private Label emptyNote() {
        Label l = new Label("No designations yet. Add one using the form.");
        l.setStyleName("empty-note");
        return l;
    }

    /** Renders inline Edit / Delete buttons and dispatches clicks by data-action. */
    private class ActionsCell extends AbstractCell<EmployeeDTO> {
        ActionsCell() { super("click"); }

        @Override
        public void render(Context context, EmployeeDTO value, SafeHtmlBuilder sb) {
            sb.appendHtmlConstant("<button type=\"button\" class=\"btn btn-edit\" data-action=\"edit\">Edit</button> "
                    + "<button type=\"button\" class=\"btn btn-delete\" data-action=\"delete\">Delete</button>");
        }

        @Override
        public void onBrowserEvent(Context context, Element parent, EmployeeDTO value,
                                   NativeEvent event, ValueUpdater<EmployeeDTO> updater) {
            if (value == null || !"click".equals(event.getType())) return;
            Element target = event.getEventTarget().cast();
            String action = target.getAttribute("data-action");
            if ("edit".equals(action)) onEdit(value);
            else if ("delete".equals(action)) onDelete(value);
        }
    }

    // ---------- Actions ----------

    private void onSubmit() {
        Long id = null;
        String idText = idBox.getText().trim();
        if (!idText.isEmpty()) {
            try { id = Long.valueOf(idText); }
            catch (NumberFormatException e) { showStatus("ID must be a whole number.", "status-error"); return; }
        }
        String name = nameBox.getText().trim();
        if (name.isEmpty()) { showStatus("Name is required.", "status-error"); nameBox.setFocus(true); return; }

        double salary;
        try { salary = Double.parseDouble(salaryBox.getText().trim()); }
        catch (NumberFormatException e) { showStatus("Salary per hour must be a number.", "status-error"); return; }
        if (salary < 0) { showStatus("Salary per hour cannot be negative.", "status-error"); return; }

        final boolean wasEditing = editing;
        setBusy(true);
        service.save(new EmployeeDTO(id, name, salary), new AsyncCallback<EmployeeDTO>() {
            @Override public void onFailure(Throwable caught) {
                setBusy(false);
                showStatus(messageOf(caught), "status-error");
            }
            @Override public void onSuccess(EmployeeDTO saved) {
                setBusy(false);
                resetForm();
                showStatus((wasEditing ? "Updated " : "Saved ") + saved.getName() + ".", "status-ok");
                loadData(false);
            }
        });
    }

    private void onEdit(EmployeeDTO e) {
        editing = true;
        idBox.setText(String.valueOf(e.getId()));
        idBox.setReadOnly(true);
        nameBox.setText(e.getName());
        salaryBox.setText(String.valueOf(e.getSalaryPerHour()));
        formTitle.setText("Edit designation " + e.getId());
        submitBtn.setText("Update");
        showStatus("", "status-info");
        nameBox.setFocus(true);
    }

    private void onDelete(final EmployeeDTO e) {
        if (!Window.confirm("Delete \"" + e.getName() + "\"? This cannot be undone.")) return;
        service.delete(e.getId(), new AsyncCallback<Void>() {
            @Override public void onFailure(Throwable caught) { showStatus(messageOf(caught), "status-error"); }
            @Override public void onSuccess(Void result) {
                if (editing && String.valueOf(e.getId()).equals(idBox.getText())) resetForm();
                showStatus("Deleted " + e.getName() + ".", "status-ok");
                loadData(false);
            }
        });
    }

    private void loadData(final boolean announceErrors) {
        service.getAll(new AsyncCallback<List<EmployeeDTO>>() {
            @Override public void onFailure(Throwable caught) {
                if (announceErrors) showStatus(messageOf(caught), "status-error"); // stay quiet during background polls
            }
            @Override public void onSuccess(List<EmployeeDTO> result) {
                List<EmployeeDTO> list = provider.getList();
                list.clear();
                list.addAll(result);
                provider.refresh();
            }
        });
    }

    // ---------- Helpers ----------

    private void resetForm() {
        editing = false;
        idBox.setText("");
        idBox.setReadOnly(false);
        nameBox.setText("");
        salaryBox.setText("");
        formTitle.setText("Add designation");
        submitBtn.setText("Submit");
    }

    private void setBusy(boolean busy) {
        submitBtn.setEnabled(!busy);
        if (busy) showStatus("Saving...", "status-info");
    }

    private void showStatus(String msg, String style) {
        status.setText(msg);
        status.setStyleName("status " + style);
    }

    private String messageOf(Throwable t) {
        if (t instanceof ServiceException) return t.getMessage();
        if (t instanceof StatusCodeException) {
            int code = ((StatusCodeException) t).getStatusCode();
            return code == 0 ? "Cannot reach the server. Check your connection."
                    : "Server error (HTTP " + code + "). Please try again.";
        }
        return "Request failed: " + t.getMessage();
    }
}