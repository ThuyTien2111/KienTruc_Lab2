package com.companyname.iuh;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.File;

public class VerbCheck {

    public static void main(String[] args) {
        String wordToCheck = "searched"; // Thay đổi từ cần kiểm tra ở đây

        try {
            // Load mô hình POS
            POSModel model = new POSModelLoader().load(new File("D:\\\\BaiTap\\\\Kientruc\\\\Lab2\\\\Lab2\\\\en-pos-maxent.bin")); // Đường dẫn tới tệp mô hình

            // Khởi tạo POSTaggerME với mô hình
            POSTaggerME tagger = new POSTaggerME(model);

            // Sử dụng tagger để nhận dạng từ loại của từ cần kiểm tra
            String[] words = new String[]{wordToCheck};
            String[] tags = tagger.tag(words);

            // Kiểm tra xem từ loại của từ đó có phải là động từ không
            boolean isVerb = tags[0].startsWith("VB");

            // In kết quả
            if (isVerb) {
                System.out.println(wordToCheck + " is a verb.");
            } else {
                System.out.println(wordToCheck + " is not a verb.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
