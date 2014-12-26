/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ui;

import com.parser.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import net.miginfocom.swing.*;

/**
 *
 * @author vigneshm
 */
public class MainWindow extends JFrame{
    JTextArea mainText;
    EntryList currentList;
    JCheckBox moreinfo;
    JCheckBox sortyear;
    JCheckBox sortcit;
    boolean searchdialogopen;
    public MainWindow(){
        searchdialogopen=false;
        MigLayout layout = new MigLayout("fillx", "[][grow,fill][]", "[]rel[][]rel[][][][][]unrel[][][][]");
        JPanel panel = new JPanel(layout);
        mainText=new JTextArea("text");
        mainText.setLineWrap(true);
        JScrollPane textScroll = new JScrollPane(mainText,    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(textScroll,"h 100% , spany ,grow ,cell 1 0");
        JButton searchBtn=new JButton("Search");
        panel.add(searchBtn,"cell 0 3 , aligny top");
        moreinfo=new JCheckBox("More Info");
        moreinfo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                updateText();
            }
        });
        panel.add(moreinfo,"cell 0 7 ,aligny top ");
        JButton sortdateBtn=new JButton("Sort by date");
        sortdateBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                sortYr();
            }
        });
        panel.add(sortdateBtn,"cell 2 0");
        sortyear=new JCheckBox("Decreasing");
        panel.add(sortyear,"cell 2 2");
        sortyear.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                sortYr();
            }
        });
        JButton sortcitBtn=new JButton("Sort by Citation");
        sortcitBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                sortCit();
            }
        });
        panel.add(sortcitBtn,"cell 2 3");
        sortcit=new JCheckBox("Decreasing");
        panel.add(sortcit,"cell 2 5");
        sortcit.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                sortCit();
            }
        });
        searchBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                searchBtnAction();
            }
        });
        JButton authorchange=new JButton("Choose Authors");
        panel.add(authorchange,"cell 2 6");
        authorchange.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                authorChangeBtnAction();
            }
        });
        JButton dateset=new JButton("Filter by Year");
        panel.add(dateset,"cell 2 7");
        dateset.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                dateSetBtnAction();
            }
        });
        panel.add(new JLabel("Citation metrics \n(from current search results) "),"cell 2 8 ,spany 2");
        JButton hindexBtn=new JButton("Get H-Index");
        panel.add(hindexBtn,"cell 2 10");
        hindexBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                getHIndex();
            }
        });
        JButton iindexBtn=new JButton("Get I-Index ");
        panel.add(iindexBtn,"cell 2 11,aligny top");
        iindexBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                getIIndex();
            }
        });
        setContentPane(panel);
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);    
    }
    public void getHIndex(){
        new SimpleDialog(this,"H-Index","H Index from current results is" +currentList.getHIndex());
    }
    public void getIIndex(){
        new SimpleDialog(this,"H-Index","H Index from current results is" +currentList.getIIndex());
    }
    public void sortYr(){
        currentList.sortByYear(!sortyear.isSelected());
        updateText();
    }
    public void sortCit(){
        currentList.sortByCitations(!sortcit.isSelected());
        updateText();
    }
    public void searchBtnAction(){
        //if(!searchdialogopen){
        //    searchdialogopen=true;
            this.new SearchDialog(this);
        //}
        
    }
    public void authorChangeBtnAction(){
        AuthorSelect x=this.new AuthorSelect(this);
        //this.currentList=x.list;
        //updateText();
    }
    public void dateSetBtnAction(){
        this.new DateSet(this);
    }
    public void search(int mode,String name,String number){
        int num=10;
        try{
            num=Integer.parseInt(number.trim());
        }catch(NumberFormatException e){
            new SimpleDialog(this,"Enter a number in number of results field!","Error !");
            searchBtnAction();
            System.out.println("number error");
        }
        try{
            currentList=parser.parsePage(mode, name, num);
            System.out.println("got "+currentList.size()+" elements from "+parser.searchString(mode, name, 0, num));
        }catch (java.net.UnknownHostException e){
            new SimpleDialog(this,"Error !","Check your Internet Connection - Unable to find host");
        }catch (Exception e){
            System.out.println(e);
            new SimpleDialog(this,"Error !","Error !");
        }
        System.out.println("updating");
        updateText();
    }
    public void updateText(){
        if(currentList==null) {
            return;
        } 
        mainText.setText("");
        System.out.println("printing "+currentList.size()+" elements");
        for(Entry e:currentList){
            if(moreinfo.isSelected()) mainText.append(e.toString()+"\n");
            else mainText.append(e.toString_basic()+"\n");
        }
    }
    public class DateSet extends JDialog{
        public void cancelBtn(){
            setVisible(false);
            dispose();
        }
        JTextField st,en;
        MainWindow parent;
        public void setBtn(String start,String end){
            int st,en;
            try{
                st=Integer.parseInt(start.trim());
                en=Integer.parseInt(end.trim());
                parent.currentList=new EntryList(parent.currentList.datesearchlist(st, en));
                parent.updateText();
            }catch(NumberFormatException e){
                parent.new SimpleDialog(parent,"Error !","Enter proper number");
            }
            setVisible(false);
            dispose();
        }
        public DateSet(MainWindow parent){
            this.parent=parent;
            JPanel panel=new JPanel(new MigLayout("","[right]rel[grow,fill]","[][][fill][fill]"));
            panel.add(new JLabel("Start Date : "),"");
            st=new JTextField();
            en=new JTextField();
            panel.add(st,"wrap");
            panel.add(new JLabel("End Date : "),"");
            panel.add(en,"wrap");
            if (parent != null) {
                Dimension parentSize = parent.getSize(); 
                Point p = parent.getLocation(); 
                setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
              }
            getContentPane().add(panel);
            JPanel buttonPane = new JPanel();
            JButton button = new JButton("OK"); 
            buttonPane.add(button,"spany"); 
            button.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    setBtn(st.getText(),en.getText());
                }
            });
            JButton cancelbutton = new JButton("Cancel"); 
            buttonPane.add(cancelbutton,"spany"); 
            cancelbutton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    cancelBtn();
                }
            });
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            pack(); 
            setVisible(true);
        }
    }
    public class AuthorSelect extends JFrame{
        EntryList list;
        List<JCheckBox> checklist;
        MainWindow p;
        public void changeAction(){
            for(JCheckBox cb:checklist){
                if(!cb.isSelected()) list.removeAuthor(cb.getText());
            }
            p.updateText();
            setVisible(false);
            dispose();
        }
        public AuthorSelect(MainWindow parent){
            list=parent.currentList;
            p=parent;
            Set<String> authorList=list.authorlist;
            JPanel panel=new JPanel(new MigLayout("ins 0 ,flowy"));
            JPanel authorPane=new JPanel(new MigLayout("flowy"));
            checklist=new ArrayList<JCheckBox>();
            for(String name:authorList){
                JCheckBox x=new JCheckBox(name);
                checklist.add(x);
                authorPane.add(x);
            }
            JScrollPane scrolllist=new JScrollPane(authorPane,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            panel.add(scrolllist,"dock north,spany,height 90%,grow");
            JButton changeBtn=new JButton("Change");
            panel.add(changeBtn);
            changeBtn.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    changeAction();
                }
            });
            panel.setSize(100, 200);
            getContentPane().add(panel, BorderLayout.SOUTH);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            pack(); 
            setVisible(true);
        }
    }
    public class SearchDialog extends JDialog{
        JTextField name,number;
        public void searchBtn(MainWindow parent){
            parent.search(1,name.getText(),number.getText());
            setVisible(false);
            parent.searchdialogopen=false;
            dispose();
        }
        public void cancelBtn(MainWindow parent){
            //parent.search(1,name.getText(),number.getText());
            setVisible(false);
            dispose();
        }
        public SearchDialog(final MainWindow parent){
            if (parent != null) {
                Dimension parentSize = parent.getSize(); 
                Point p = parent.getLocation(); 
                setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
              }
            JPanel messagePane = new JPanel(new MigLayout("","[right]rel[grow,fill]","[][][]"));
            messagePane.add(new JLabel("Author Name:"),"");
            messagePane.add((name=new JTextField("Enter name:")),"wrap");
            messagePane.add(new JLabel("Number of Results:"),"");
            messagePane.add((number=new JTextField("10")),"wrap");
            getContentPane().add(messagePane);
            JPanel buttonPane = new JPanel();
            JButton button = new JButton("OK"); 
            buttonPane.add(button,"spany"); 
            button.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    searchBtn(parent);
                }
            });
            JButton cancelbutton = new JButton("Cancel"); 
            buttonPane.add(cancelbutton,"spany"); 
            cancelbutton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    cancelBtn(parent);
                }
            });
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            pack(); 
            setVisible(true);
        }
    }
    public class SimpleDialog extends JDialog implements ActionListener {
        public SimpleDialog(JFrame parent, String title, String message) {
            super(parent, title, true);
            if (parent != null) {
                Dimension parentSize = parent.getSize(); 
                Point p = parent.getLocation(); 
                setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
            }
            JPanel messagePane = new JPanel();
            messagePane.add(new JLabel(message));
            getContentPane().add(messagePane);
            JPanel buttonPane = new JPanel();
            JButton button = new JButton("OK"); 
            buttonPane.add(button); 
            button.addActionListener(this);
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            pack(); 
            setVisible(true);
        }
        public void actionPerformed(ActionEvent e) {
            setVisible(false); 
            dispose(); 
        }
    }
    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow win=new MainWindow();
                win.setVisible(true);
            }
        });
    }
}
