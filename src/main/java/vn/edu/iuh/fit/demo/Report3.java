package vn.edu.iuh.fit.demo;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Strings;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import vn.edu.iuh.fit.tool.DirExplorer;

public class Report3 {

	private static PrintWriter outputWriter;

	public static void listMethodCalls(File projectDir, String outputFilePath) {
		try {
			outputWriter = new PrintWriter(new FileWriter(outputFilePath));

			new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
				outputWriter.println(path);
				outputWriter.println(Strings.repeat("=", path.length()));
				try {
					new VoidVisitorAdapter<Object>() {
						// Cau 1
						@Override
						public void visit(PackageDeclaration n, Object arg) {
							super.visit(n, arg);
							String packageName = n.getNameAsString();
							if (!packageName.matches("com\\.companyname\\.[a-zA-Z_$][a-zA-Z\\d_$]*")) {
								outputWriter.println("Invalid package name: " + packageName);
							}
						}

						// Cau 2
						@Override
						public void visit(ClassOrInterfaceDeclaration n, Object arg) {
							super.visit(n, arg);
							String className = n.getNameAsString();
							if (!isValidClassName(className)) {
								outputWriter.println("Invalid class name: " + className);
							}
							// Cau 3
							if (!n.getComment().isPresent()) {
								outputWriter.println("Invalid Comment: Class '" + className + "' has no comment.");
							} else {
								String commentContent = n.getComment().get().getContent();
								if (!commentContent.contains("Author") || !commentContent.contains("Created-date")) {
									outputWriter.println("Invalid Comment: Class '" + className
											+ "' does not contain required information (author, created-date).");
								}
							}
						}

						// Cau 4
						@Override
						public void visit(FieldDeclaration n, Object arg) {
							super.visit(n, arg);
							List<String> fieldNames = new ArrayList<>();
							for (VariableDeclarator variableDeclarator : n.getVariables()) {
								fieldNames.add(variableDeclarator.getNameAsString());
							}
							for (String string : fieldNames) {
								if (!isValidFieldName(string)) {
									outputWriter.println("Invalid field name: " + string);
								}
							}

						}

						// Cau 6
						@Override
						public void visit(MethodDeclaration n, Object arg) {
							// TODO Auto-generated method stub
							super.visit(n, arg);
							String methodName = n.getNameAsString();
							if (!isValidMethodName(methodName)) {
								outputWriter.println("Invalid method name: " + methodName);
							}
						}

						private boolean isValidMethodName(String methodName) {
							POSModel model = new POSModelLoader()
									.load(new File("D:\\BaiTap\\Kientruc\\Lab2\\Lab2\\en-pos-maxent.bin"));
							POSTaggerME tagger = new POSTaggerME(model);
							String[] words = methodName.split("(?=[A-Z])");
							String firstWord = words[0];
							// Kiểm tra xem giá trị đầu tiên của mảng có phải bắt đầu bằng chữ in hoa hay
							// không
							if (Character.isUpperCase(firstWord.charAt(0))) {
								return false;
							}
							// Kiểm tra xem giá trị đầu tiên của mảng có phải là động từ hay không
							String[] tags = tagger.tag(new String[] { firstWord });
							// Kiểm tra xem có từ loại nào là động từ không
							return Arrays.stream(tags).anyMatch(tag -> tag.startsWith("VB"));
						}

						private boolean isValidFieldName(String fieldName) {
							if (!fieldName.matches("[a-z][a-zA-Z]*")) {
								return false;
							}
							POSModel model = new POSModelLoader()
									.load(new File("D:\\BaiTap\\Kientruc\\Lab2\\Lab2\\en-pos-maxent.bin"));
							POSTaggerME tagger = new POSTaggerME(model);
							String[] words = fieldName.split("(?=[A-Z])");
							String[] tags = tagger.tag(words);

							return Arrays.stream(tags).anyMatch(tag -> tag.startsWith("NN"));
						}

						private boolean isValidClassName(String className) {
							// Kiểm tra xem class có tên đúng định dạng không
							if (!className.matches("[A-Z][a-zA-Z]*")) {
								return false;
							}

							// Sử dụng OpenNLP để kiểm tra xem tên class có phải là danh từ không
							POSModel model = new POSModelLoader()
									.load(new File("D:\\BaiTap\\Kientruc\\Lab2\\Lab2\\en-pos-maxent.bin"));
							POSTaggerME tagger = new POSTaggerME(model);
							String[] words = className.split("(?=[A-Z])");
							String[] tags = tagger.tag(words);

							// Kiểm tra xem có từ loại nào là danh từ không
							return Arrays.stream(tags).anyMatch(tag -> tag.startsWith("NN"));
						}

						// Xuất báo cáo
					}.visit(StaticJavaParser.parse(file), null);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}).explore(projectDir);

			outputWriter.close(); // Close the PrintWriter after writing to the file
		} catch (Exception e) {
			e.printStackTrace(); // Handle exception appropriately
		}
	}

	public static void main(String[] args) {
		File projectDir = new File("D:\\BaiTap\\Kientruc\\Lab2\\Lab2");
		String outputFilePath = "D:\\BaiTap\\Kientruc\\Lab2\\Lab2\\output.txt";
		listMethodCalls(projectDir, outputFilePath);
	}
}
