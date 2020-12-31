package il.ac.hit.view;

import il.ac.hit.model.Category;
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

import static il.ac.hit.view.Currency.*;

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
    private JLabel title;
    private JLabel description;
    private JLabel cost;
    private JLabel date;
    private JLabel category;
    private JLabel currency;
    private JTextField descriptionText;
    private JTextField costText;
    private JButton add;
    private JButton homePage;
    private CreateJDatePicker datePicker;
    private JComboBox chosenCategory;
    private JComboBox chosenCurrency;
    private ImageIcon okIcon;

    //Create all the components in this frame.
    AddCostOrIncomeFrame(IViewModel vm) {
        setVm(vm);
        panelNorth = new JPanel();
        panelCenter = new JPanel();
        panelSouth = new JPanel();
        okIcon = new ImageIcon("ok.png");
        frame = new JFrame("Add New Category");
        title = new JLabel("Add Income Or Expense");
        description = new JLabel ("description:");
        descriptionText = new JTextField(40);
        cost = new JLabel("cost:");
        costText = new JTextField(40);
        date = new JLabel("date:");
        category = new JLabel("category:");
        currency = new JLabel("currency:");

        //call to function that charges the categories from the db to this JComboBox
        chosenCategory = createChosenCategory();
        //call to function that fill this JComboBox
        chosenCurrency = createChosenCurrency();

        add = new JButton("add");
        homePage = new JButton("homePage");

        descriptionPanel = new JPanel();
        costPanel = new JPanel();
        datePanel = new JPanel();
        categoryPanel = new JPanel();
        currencyPanel= new JPanel();
    }

    public void setVm(IViewModel vm) {
        this.vm = vm;
    }

    //the function that charges the categories from the db to the category JComboBox
    public JComboBox createChosenCategory() {
        JComboBox categoriesJCombox = null;
        ArrayList<Category> categories = vm.getAllCategories();
        String[] categoriesNames = new String[categories.size()];
        for(int i =0;i<categories.size();i++){
            categoriesNames[i] = categories.get(i).getCategoryName();
        }
        categoriesJCombox = new JComboBox(categoriesNames);
        return categoriesJCombox;
    }
    //the function that fill the currency JComboBox
    public JComboBox createChosenCurrency(){
        JComboBox currenciesJCombox = null;
        String[] Currencies = {"ILS","USD","EUR"};
        currenciesJCombox = new JComboBox(Currencies);
        return currenciesJCombox;
    }

    //Organize all the components in this frame.
    public void start(){
        frame.setLayout(new BorderLayout());
        frame.setSize(1000,600);

        //north panel
        title.setFont(new Font("serif", Font.PLAIN, 40));
        panelNorth.add(title);
        panelNorth.setBackground(new Color(255,255,255));
        frame.add(panelNorth, BorderLayout.NORTH);

        //center panel
        panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.PAGE_AXIS));

        descriptionPanel.add(description);
        descriptionPanel.add(descriptionText);
        descriptionPanel.setBackground(new Color(240,240,255));
        panelCenter.add(descriptionPanel);

        costPanel.add(cost);
        costPanel.add(costText);
        costPanel.setBackground(new Color(240,240,255));
        panelCenter.add(costPanel);

        currencyPanel.add(currency);
        currencyPanel.add(chosenCurrency);
        currencyPanel.setBackground(new Color(240,240,255));
        panelCenter.add(currencyPanel);

        datePanel.add(date);
        datePicker = new CreateJDatePicker(datePanel);
        datePanel.setBackground(new Color(240,240,255));
        panelCenter.add(datePanel);

        categoryPanel.add(category);
        categoryPanel.add(chosenCategory);
        categoryPanel.setBackground(new Color(240,240,255));
        panelCenter.add(categoryPanel);

        frame.add(panelCenter, BorderLayout.CENTER);

        //south panel
        panelSouth.setLayout((new FlowLayout()));
        panelSouth.add(add);
        panelSouth.add(homePage);
        panelSouth.setBackground(new Color(240,240,255));
        frame.add(panelSouth, BorderLayout.SOUTH);

        //Adding events listeners.
        //add expense or incomes to the DB.
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Take from the user the description of the CostOrIncome item.
                String desc = descriptionText.getText();
                /*Take from the user the cost of the CostOrIncome item.
                if it's not number a message will be displayed*/
                double cost = 0;
                try {
                    cost = Double.parseDouble(costText.getText());

                    /*Take from the user the date.
                    if no date inserted a message will be displayed*/
                    Date date = null;
                    try {
                        date = new SimpleDateFormat("dd-MM-yyyy").parse(datePicker.datePicker.getJFormattedTextField().getText());

                        //Take from the user the category of the CostOrIncome item.
                        Category category = new Category(chosenCategory.getSelectedItem().toString());

                        //Take from the user the currency of the cost and converts it to shekels(ILS).
                        String currency = chosenCurrency.getSelectedItem().toString();
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
                        //create the item according to the details of the user
                        CostOrIncome costOrIncome = new CostOrIncome(desc, cost, new java.sql.Date(date.getYear(), date.getMonth(), date.getDay()), category);

                        //add the item to the db
                        vm.addCostOrIncome(costOrIncome);
                    } catch (ParseException e) {
                        //If the user don't give us a date.
                        JOptionPane.showMessageDialog(null, e.getMessage(), "Error!", HEIGHT);
                    }
                } catch (NumberFormatException e) {
                    //If the user don't give us a number in the cost.
                    JOptionPane.showMessageDialog(null, "the cost must be a double", "Error!", HEIGHT);
                }
            }
        });

        //This button return the user to the StartFrame.
        homePage.addActionListener(new ActionListener() {
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
