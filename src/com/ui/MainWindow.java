/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ui;

import com.parser.*;
import java.awt.BorderLayout;
import java.awt.Component;
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
    JList mainText;
    Vector<String> listData;
    EntryList currentList;
    JCheckBox sortyear;
    JCheckBox sortcit;
    JScrollPane textScroll;
    boolean searchdialogopen;
    boolean authordialogopen;
    boolean datedialogopen;
    public MainWindow(EntryList elist){
        this();
        this.currentList=elist;
        updateText();
    }
    public MainWindow(){
        searchdialogopen=false;
        MigLayout layout = new MigLayout("fillx", "[][grow,fill][]", "[]rel[][]rel[][][][][]unrel[][][][]");
        JPanel panel = new JPanel(layout);
        listData=new Vector<>();
        mainText=new JList();
        textScroll = new JScrollPane(mainText,    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mainText.setCellRenderer(new MyCellRenderer(textScroll.getWidth()));
        mainText.setListData(listData);
        panel.add(textScroll,"h 100% , spany ,grow ,cell 1 0");
        JButton searchBtn=new JButton("Search");
        panel.add(searchBtn,"cell 0 3 , aligny top");
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
        JButton openEntryBtn=new JButton("Open entry");
        panel.add(openEntryBtn,"cell 0 7,aligny top");
        openEntryBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                openEntry();
            }
        });
        setContentPane(panel);
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);    
    }
    public void getHIndex(){
        if(currentList==null){
            new SimpleDialog(this,"Error !","Try doing a search !");
            return ;
        }
        new SimpleDialog(this,"H-Index","H Index from current results is " +currentList.getHIndex());
    }
    public void getIIndex(){
        if(currentList==null){
            new SimpleDialog(this,"Error !","Try doing a search !");
            return ;
        }
        new SimpleDialog(this,"I-Index","I Index from current results is " +currentList.getIIndex());
    }
    public void sortYr(){
        if(currentList==null){
            new SimpleDialog(this,"Error !","Try doing a search !");
            return ;
        }
        currentList.sortByYear(!sortyear.isSelected());
        updateText();
    }
    public void sortCit(){
        if(currentList==null){
            new SimpleDialog(this,"Error !","Try doing a search !");
            return ;
        }
        currentList.sortByCitations(!sortcit.isSelected());
        updateText();
    }
    public void searchBtnAction(){
        if(!searchdialogopen){
            searchdialogopen=true;
            this.new SearchDialog(this);
        }
    }
    public void authorChangeBtnAction(){
        if(currentList==null){
            new SimpleDialog(this,"Error !","Try doing a search !");
            return ;
        }
        if(!authordialogopen){
            authordialogopen=true;
            this.new AuthorSelect(this);
        }
    }
    public void dateSetBtnAction(){
        if(currentList==null){
            new SimpleDialog(this,"Error !","Try doing a search !");
            return ;
        }
        if(!datedialogopen){
            datedialogopen=true;
            this.new DateSet(this);
        }
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
            //System.out.println("got "+currentList.size()+" elements from "+parser.searchString(mode, name, 0, num));
        }catch (java.net.UnknownHostException e){
            new SimpleDialog(this,"Error !","Check your Internet Connection - Unable to find host");
        }catch (Exception e){
            System.out.println(e);
            new SimpleDialog(this,"Error !","Error !");
        }
        //System.out.println("updating");
        updateText();
    }
    public void updateText(){
        if(currentList==null){
            new SimpleDialog(this,"Error !","Try doing a search !");
            return ;
        }
        System.out.println("printing "+currentList.size()+" elements");
        listData.clear();
        for(Entry e:currentList){
            listData.add(e.title);
        }
        mainText.setCellRenderer(new MyCellRenderer((int)(textScroll.getWidth()*0.7)));
        mainText.setListData(listData);
        textScroll.revalidate();
        textScroll.repaint();
    }
    public void openEntry(){
        int index=mainText.getSelectedIndex();
        if(index<0 || index>=listData.size()) return;
        String selTitle=listData.elementAt(index);
        Entry selEntry=null;
        for(Entry e:currentList){
            if(e.title.equals(selTitle)){
                selEntry=e;
                break;
            }
        }
        if(selEntry==null) return;
        EntryDisplay display=new EntryDisplay(selEntry,this);
        display.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        display.pack(); 
        display.setVisible(true);
    }
    public class DateSet extends JDialog{
        
        public void cancelBtn(){
            parent.datedialogopen=false;
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
            parent.datedialogopen=false;
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
            p.authordialogopen=false;
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
        int mode;
        public void searchBtn(MainWindow parent){
            parent.search(mode,name.getText(),number.getText());
            setVisible(false);
            parent.searchdialogopen=false;
            dispose();
        }
        public void cancelBtn(MainWindow parent){
            //parent.search(1,name.getText(),number.getText());
            setVisible(false);
            parent.searchdialogopen=false;
            dispose();
        }
        public SearchDialog(final MainWindow parent){
            mode=1;
            if (parent != null) {
                Dimension parentSize = parent.getSize(); 
                Point p = parent.getLocation(); 
                setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
              }
            JPanel messagePane = new JPanel(new MigLayout("","[right]rel[grow,fill][grow,fill]","[][][][]"));
            messagePane.add(new JLabel("Name:"),"");
            messagePane.add((name=new JTextField("Enter name:")),"wrap");
            messagePane.add(new JLabel("Number of Results:"),"");
            messagePane.add((number=new JTextField("10")),"wrap");
            JRadioButton authorOpt=new JRadioButton("Author Search");
            JRadioButton publicOpt=new JRadioButton("Publication Search");
            ButtonGroup optGroup=new ButtonGroup();
            authorOpt.setSelected(true);
            optGroup.add(authorOpt);
            optGroup.add(publicOpt);
            messagePane.add(authorOpt,"spany");
            messagePane.add(publicOpt,"spany");
            authorOpt.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    mode=1;
                }
            });
            publicOpt.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    mode=2;
                }
            });
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
    class MyCellRenderer extends DefaultListCellRenderer {
   public static final String HTML_1 = "<html><body style='width: ";
   public static final String HTML_2 = "px'>";
   public static final String HTML_3 = "</html>";
   private int width;

   public MyCellRenderer(int width) {
      this.width = width;
   }

   @Override
   public Component getListCellRendererComponent(JList list, Object value,
         int index, boolean isSelected, boolean cellHasFocus) {
      String text = HTML_1 + String.valueOf(width) + HTML_2 + value.toString()
            + HTML_3;
      return super.getListCellRendererComponent(list, text, index, isSelected,
            cellHasFocus);
   }

}
    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow win=new MainWindow();
                win.setTitle("Citation Analyzer");
                win.setVisible(true);
            }
        });
    }
}
