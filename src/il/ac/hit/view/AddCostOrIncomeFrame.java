package il.ac.hit.view;

import il.ac.hit.model.Category;
import il.ac.hit.model.CostManagerException;
import il.ac.hit.model.CostOrIncome;
import il.ac.hit.viewModel.IViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static il.ac.hit.view.Currency.*;

/**
 * This is the frame of adding income or cost.
 * @author Inbal mishal and Tal levi
 */
public class AddCostOrIncomeFrame extends JFrame {
    private IViewModel vm;
    private JFrame frame;
    private JPanel panelNorth;
    private JPanel panelCenter;
    private JPanel panelSouth;
    private JPanel descriptionPanel;
    private JPanel costPanel;
    private JPanel datePanel;
    private JPanel categoryPanel;
    private JPanel currencyPanel;
    private JLabel lbTitle;
    private JLabel lbDescription;
    private JLabel lbCost;
    private JLabel lbDate;
    private JLabel lbCategory;
    private JLabel lbCurrency;
    private JTextField tfDescription;
    private JTextField tfCost;
    private JButton btAdd;
    private JButton btHomePage;
    private CreateJDatePicker datePicker;
    private JComboBox cbChosenCategory;
    private JComboBox cbChosenCurrency;
    private JCheckBox cbExpenses;
    private JCheckBox cbIncomes;


    /**
     * Create all the components in this frame.
     * @param vm Represent the viewModel that connected to the model.
     */
    AddCostOrIncomeFrame(IViewModel vm) {
        setVm(vm);
        panelNorth = new JPanel();
        panelCenter = new JPanel();
        panelSouth = new JPanel();
        frame = new JFrame("Add New Category");
        lbTitle = new JLabel("Add Income Or Expense");
        lbDescription = new JLabel ("description:");
        tfDescription = new JTextField(40);
        lbCost = new JLabel("cost:");
        tfCost = new JTextField(40);
        cbIncomes = new JCheckBox("income ");
        cbExpenses = new JCheckBox(("expense "));
        lbDate = new JLabel("date:");
        lbCategory = new JLabel("category:");
        lbCurrency = new JLabel("currency:");

        //call to function that charges the categories from the db to this JComboBox
        cbChosenCategory = createChosenCategory();
        //call to function that fill this JComboBox
        cbChosenCurrency = createChosenCurrency();

        btAdd = new JButton("add");
        btHomePage = new JButton("homePage");

        descriptionPanel = new JPanel();
        costPanel = new JPanel();
        datePanel = new JPanel();
        categoryPanel = new JPanel();
        currencyPanel= new JPanel();
    }

    /**
     * Set the viewModel parameter.
     * @param vm Represent the view model that connected to the model.
     */
    public void setVm(IViewModel vm) {
        this.vm = vm;
    }

    //the function that charges the categories from the db to the category JComboBox
    private JComboBox createChosenCategory() {
        JComboBox categoriesJCombox;
        ArrayList<Category> categories = vm.getAllCategories();
        String[] categoriesNames = new String[categories.size()];
        for(int i =0;i<categories.size();i++){
            categoriesNames[i] = categories.get(i).getCategoryName();
        }
        categoriesJCombox = new JComboBox(categoriesNames);
        return categoriesJCombox;
    }
    //the function that fill the currency JComboBox
    private JComboBox createChosenCurrency(){
        JComboBox currenciesJCombox;
        String[] Currencies = {"ILS","USD","EUR"};
        currenciesJCombox = new JComboBox(Currencies);
        return currenciesJCombox;
    }

    /**
     * Organize all the components in this frame and create events listeners to the buttons.
     */
    public void start(){
        frame.setLayout(new BorderLayout());
        frame.setSize(1000,600);

        //north panel
        lbTitle.setFont(new Font("serif", Font.PLAIN, 40));
        panelNorth.add(lbTitle);
        panelNorth.setBackground(new Color(255,255,255));
        frame.add(panelNorth, BorderLayout.NORTH);

        //center panel
        panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.PAGE_AXIS));

        descriptionPanel.add(lbDescription);
        descriptionPanel.add(tfDescription);
        descriptionPanel.setBackground(new Color(240,240,255));
        panelCenter.add(descriptionPanel);

        costPanel.add(lbCost);
        costPanel.add(tfCost);
        costPanel.add(cbIncomes);
        costPanel.add(cbExpenses);
        costPanel.setBackground(new Color(240,240,255));
        panelCenter.add(costPanel);

        currencyPanel.add(lbCurrency);
        currencyPanel.add(cbChosenCurrency);
        currencyPanel.setBackground(new Color(240,240,255));
        panelCenter.add(currencyPanel);

        datePanel.add(lbDate);
        datePicker = new CreateJDatePicker(datePanel);
        datePanel.setBackground(new Color(240,240,255));
        panelCenter.add(datePanel);

        categoryPanel.add(lbCategory);
        categoryPanel.add(cbChosenCategory);
        categoryPanel.setBackground(new Color(240,240,255));
        panelCenter.add(categoryPanel);

        frame.add(panelCenter, BorderLayout.CENTER);

        //south panel
        panelSouth.setLayout((new FlowLayout()));
        panelSouth.add(btAdd);
        panelSouth.add(btHomePage);
        panelSouth.setBackground(new Color(240,240,255));
        frame.add(panelSouth, BorderLayout.SOUTH);

        //Adding events listeners.
        //add expense or incomes to the DB.
        btAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Take from the user the description of the CostOrIncome item.
                String desc = tfDescription.getText();
                /*Take from the user the cost of the CostOrIncome item.
                if it's not number a message will be displayed*/
                double cost;
                try {
                    if (desc.length() == 0)
                        throw new CostManagerException("item description must have at least one letter");

                    try {
                        cost = Double.parseDouble(tfCost.getText());

                        /*Take from the user the date.
                        if no date inserted a message will be displayed*/
                        Date date;
                        try {
                            date = new SimpleDateFormat("dd-MM-yyyy").parse(datePicker.getDatePicker().getJFormattedTextField().getText());

                            //Take from the user the category of the CostOrIncome item.
                            Category category = new Category(Objects.requireNonNull(cbChosenCategory.getSelectedItem()).toString());

                            //Take from the user the currency of the cost and converts it to shekels(ILS).
                            String currency = Objects.requireNonNull(cbChosenCurrency.getSelectedItem()).toString();
                            switch (currency) {
                                case "EUR":
                                    EUR.setExchangeRate(3.9355);
                                    cost = EUR.convertToShekels(cost);
                                    break;
                                case "USD":
                                    USD.setExchangeRate(3.2224);
                                    cost = USD.convertToShekels(cost);
                                    break;
                            }
                            boolean checkedIncomes, checkedExpenses;
                            checkedIncomes = cbIncomes.isSelected();
                            checkedExpenses = cbExpenses.isSelected();

                            //create the item according to the details of the user and add the item to the db.
                            if ((checkedExpenses && checkedIncomes) || (!checkedExpenses && !checkedIncomes)) {
                                JOptionPane.showMessageDialog(null, "Choose whether it is an expense or an income !", "Error!", JOptionPane.WARNING_MESSAGE);
                            } else if (checkedExpenses) {
                                CostOrIncome costOrIncome = new CostOrIncome(desc, -cost, new java.sql.Date(date.getYear(), date.getMonth(), date.getDay()), category);
                                vm.addCostOrIncome(costOrIncome);
                            } else {
                                CostOrIncome costOrIncome = new CostOrIncome(desc, cost, new java.sql.Date(date.getYear(), date.getMonth(), date.getDay()), category);
                                vm.addCostOrIncome(costOrIncome);
                            }

                        } catch (ParseException e) {
                            //If the user don't give us a date.
                            JOptionPane.showMessageDialog(null, "You need to put a date", "Error!", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (NumberFormatException e) {
                        //If the user don't give us a number in the cost.
                        JOptionPane.showMessageDialog(null, "The cost must be a number", "Error!", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (CostManagerException e) {
                    JOptionPane.showMessageDialog(null, "You need write description", "Error!", JOptionPane.WARNING_MESSAGE);
                }

            }
        });

        //This button return the user to the StartFrame.
        btHomePage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                StartFrame startFrame = new StartFrame(vm);
                startFrame.start();
                frame.dispose();
            }
        });

        //When the user close the screen the program shutdown.
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setVisible(true);
    }

}
