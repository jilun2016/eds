package com.eds.ma.bis;

import java.io.FileInputStream;

import java.io.FileNotFoundException;

import java.io.FileOutputStream;

import java.io.IOException;

import java.util.HashMap;

import java.util.List;

import java.util.Map;

import java.util.Map.Entry;

import com.xcrm.common.util.ListUtil;
import org.apache.commons.collections.ListUtils;
import org.apache.poi.POIXMLDocument;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import org.apache.poi.openxml4j.opc.OPCPackage;

import org.apache.poi.util.Units;

import org.apache.poi.xwpf.usermodel.BreakType;

import org.apache.poi.xwpf.usermodel.Document;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import org.apache.poi.xwpf.usermodel.XWPFRun;

import org.apache.poi.xwpf.usermodel.XWPFTable;

import org.apache.poi.xwpf.usermodel.XWPFTableCell;

import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import org.apache.xmlbeans.XmlCursor;

public class WordUtil2007 {

    /**
     * 
     * 根据指定的参数值、模板，生成 word 文档
     * 
     * 
     * 
     * @param param
     * 
     *            需要替换的变量
     * 
     * @param template
     * 
     *            模板
     */

    public static XWPFDocument generateWord(Map<String, Object> param,String template) {
        CustomXWPFDocument doc = null;
        try {
            OPCPackage pack = POIXMLDocument.openPackage(template);
            doc = new CustomXWPFDocument(pack);
            if (param != null && param.size() > 0) {
                // 处理段落
                List<XWPFParagraph> paragraphList = doc.getParagraphs();
                processParagraphs(paragraphList, param, doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * 
     * 处理段落中文本，替换文本中定义的变量；
     * 
     * 
     * 
     * @param paragraphList
     * 
     *            段落列表
     * 
     * @param param
     * 
     *            需要替换的变量及变量值
     * 
     * @param doc
     * 
     *            需要替换的DOC
     */

    public static void processParagraphs(List<XWPFParagraph> paragraphList,Map<String, Object> param, XWPFDocument doc) {
        if (paragraphList != null && paragraphList.size() > 0) {
            for (XWPFParagraph paragraph : paragraphList) {
                List<XWPFRun> runs = paragraph.getRuns();
                for (XWPFRun run : runs) {
                    String text = run.getText(0);
                    if (text != null) {
                        boolean isSetText = false;
                        for (Entry<String, Object> entry : param.entrySet()) {
                            String key = entry.getKey();
                            if (text.indexOf(key) != -1) {
                                isSetText = true;
                                Object value = entry.getValue();
                                if (value instanceof String) {// 文本替换
                                    text = text.replace(key, value.toString());
                                }
                            }
                        }
                        if (isSetText) {
                            run.setText(text, 0);
                        }
                    }
                }
            }
        }
    }

    /**
     * 
     * 在定位的位置插入表格；
     * 
     * 
     * 
     * @param key
     * 
     *            定位的变量值
     * 
     * @param doc
     * 
     *            需要替换的DOC
     */

    public static void insertTab(String key, XWPFDocument doc2) {
        List<XWPFParagraph> paragraphList = doc2.getParagraphs();
        if (paragraphList != null && paragraphList.size() > 0) {
            for (XWPFParagraph paragraph : paragraphList) {
                List<XWPFRun> runs = paragraph.getRuns();
                for (XWPFRun run : runs) {
                    String text = run.getText(0);
                    if (text != null) {
                        if (text.indexOf(key) >= 0) {
                            XmlCursor cursor = paragraph.getCTP().newCursor();
                            XWPFTable tableOne = doc2.insertNewTbl(cursor);// ---这个是关键
                            // XWPFTable tableOne =
                            // paragraph.getDocument().createTable();
                            XWPFTableRow tableOneRowOne = tableOne.getRow(0);
                            tableOneRowOne.getCell(0).setText("序号");
                            tableOneRowOne.addNewTableCell().setText("测试时间");
                            tableOneRowOne.addNewTableCell().setText("采样位置描述");
                            tableOneRowOne.addNewTableCell().setText("采样位置图片");
                            tableOneRowOne.addNewTableCell().setText("现场温度");
                            tableOneRowOne.addNewTableCell().setText("甲醛浓度");
                            tableOneRowOne.addNewTableCell().setText("判定");
                            XWPFTableRow tableOneRowTwo = tableOne.createRow();
                            tableOneRowTwo.getCell(0).setText("第二行第一列");
                            tableOneRowTwo.getCell(1).setText("第二行第二列");
                            // tableOneRowTwo.getCell(2).setText("第2行第3列");
                            XWPFTableRow tableOneRow3 = tableOne.createRow();
                            // ---顺序增加行后，忽略第1、2单元格，直接插入3、4
                            tableOneRow3.addNewTableCell().setText("第三行第3列");
                            tableOneRow3.addNewTableCell().setText("第三行第4列");
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * 第一个表格增加行
     *
     * @param doc2 需要替换的DOC
     */

    public static void addExistTableRow(XWPFDocument doc2) {
        List<XWPFTable> tables = doc2.getTables();
        if (ListUtil.isNotEmpty(tables)) {
            XWPFTable table = tables.get(0);
            //表格的插入行有两种方式，这里使用addNewRowBetween，因为这样会保留表格的样式，
            // 就像我们在word文档的表格中插入行一样。
            // 注意这里不要使用insertNewTableRow方法插入新行，这样插入的新行没有样式，很难看
            //获取到刚刚插入的行
            XWPFTableRow row=table.createRow();
            //设置单元格内容
            row.getCell(0).setText("11111");
            //设置单元格内容
            row.getCell(1).setText("22222");
            //设置单元格内容
            row.getCell(2).setText("33333");
        }
    }


    public static void insertImage(String key, XWPFDocument doc) {
        List<XWPFParagraph> paragraphList = doc.getParagraphs();
        try {
            if (paragraphList != null && paragraphList.size() > 0) {
                for (XWPFParagraph paragraph : paragraphList) {
                    List<XWPFRun> runs = paragraph.getRuns();
                    for (XWPFRun run : runs) {
                        String text = run.getText(0);
                        if (text != null) {
                            if (text.indexOf(key) >= 0) {
                                run.addBreak();
                                run.addPicture(
                                new FileInputStream("c:/11.jpg"),
                                        Document.PICTURE_TYPE_JPEG,
                                        "c:/11.jpg", Units.toEMU(200),
                                        Units.toEMU(200)); // 200x200 pixels
                                run.addBreak(BreakType.PAGE);
                            }
                        }
                    }
                }
            }
        } catch (InvalidFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 
     * 测试用方法
     */
    public static void main(String[] args) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("${nickName}", "高岩");
        param.put("${deviceCode}", "E1021");

//      Map<String, Object> twocode = new HashMap<String, Object>();
//      twocode.put("width", 100);
//      twocode.put("height", 100);
//      twocode.put("type", "png");
        XWPFDocument doc = WordUtil2007.generateWord(param, "d:\\test.docx");
        WordUtil2007.addExistTableRow(doc); // /----------创建表
//      WordUtil2007.insertImage("${image}", doc); // /----------创建图

//        // ------替换多余的标志位----//

        param = new HashMap<String, Object>();
//        param.put("${test}", "下一个段落");
        param.put("${tableFlag}", "");
//        param.put("${image}", "");
        WordUtil2007.processParagraphs(doc.getParagraphs(), param, doc);
        FileOutputStream fopts = new FileOutputStream("d:\\2007-2.docx");
        doc.write(fopts);

    }

}