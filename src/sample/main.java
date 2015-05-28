package sample;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;

public class main {

    static Load SecondThread;
    public static JLabel label5 = new JLabel("ИДЁТ ПОДСЧЁТ");
    public static JLabel label4 = new JLabel("Оплаты+ДМС+Долги:");
    public static JLabel label3 = new JLabel("Оплаты+ДМС:");
    public static JLabel label2  = new JLabel("Долги:");
    public static JLabel label1  = new JLabel("ДМС:");
    public static JLabel label0  = new JLabel("Оплаты:");
    public static MaskFormatter mf1;
    static {
        try {
            mf1 = new MaskFormatter("####-##-##");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public static JFormattedTextField startdate  = new JFormattedTextField (mf1);
    public static JFormattedTextField enddate  = new JFormattedTextField (mf1);
    public static JButton button = new JButton("Показать");;

    private static void createAndShowGUI() throws ParseException {
        JFrame frame = new JFrame("Выручка");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Font font = new Font("Serif",Font.PLAIN,18);
        final JLabel labelot = new JLabel("Начальная дата");
        final JLabel labeldo = new JLabel("Конечная дата");
        final JTextArea text = new JTextArea("Внимание! Первое поле - Начальная дата. \n" +
                "Второе поле - Конечная дата + 1 день. \n" +
                "Чтобы посмотреть один день, например 26.02.2015 года, \n" +
                "Нужно в первое поле указать: 2015-02-26 \n" +
                "а во второе на один день больше: 2015-02-27");
        startdate.setText(String.valueOf(Date.valueOf(LocalDate.now())));
        enddate.setText(String.valueOf(Date.valueOf(LocalDate.now().plusDays(1))));
        frame.setLayout(null);
        label0.setFont(font);
        label1.setFont(font);
        label2.setFont(font);
        label3.setFont(font);
        label4.setFont(font);
        label5.setFont(font);
        frame.setBounds(200, 200, 350, 260);
        startdate.setBounds(0,20,100,30);
        enddate.setBounds(100,20,100,30);
        button.setBounds(200, 0, 150, 50);
        label0.setBounds(0,50, 350, 20);
        label1.setBounds(0,70,350,20);
        label2.setBounds(0,90, 350, 20);
        label3.setBounds(0,110, 350, 20);
        label4.setBounds(0,130, 350, 20);
        label5.setBounds(200,50, 150, 20);
        labelot.setBounds(0,0, 100, 20);
        labeldo.setBounds(100,0, 100,20);
        text.setBounds(0,150, 350, 200);
        text.setEditable(false);
        frame.getContentPane().add(startdate);
        frame.getContentPane().add(labelot);
        frame.getContentPane().add(labeldo);
        frame.getContentPane().add(enddate);
        frame.getContentPane().add(label0);
        frame.getContentPane().add(label1);
        frame.getContentPane().add(label2);
        frame.getContentPane().add(label3);
        frame.getContentPane().add(label4);
        frame.getContentPane().add(label5);
        frame.getContentPane().add(text);
        frame.getContentPane().add(button);
        label5.setVisible(false);
        frame.setResizable(false);
        button.addActionListener(e -> {
            label5.setVisible(true);
            button.setEnabled(false);
            startdate.setEnabled(false);
            enddate.setEnabled(false);
            SecondThread = new Load();	//Создание потока
            Thread SecondT = new Thread(SecondThread);
            SecondT.start();					//Запуск потока
            });
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                createAndShowGUI();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }
}
