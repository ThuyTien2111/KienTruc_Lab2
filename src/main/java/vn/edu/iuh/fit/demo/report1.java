package vn.edu.iuh.fit.demo;

import java.io.File;

import vn.edu.iuh.fit.tool.DirExplorer;

public class report1 {

	public static void main(String[] args) {
		File projectDir = new File("C:\\Users\\Student\\eclipse-workspace\\Lab2");
		new  DirExplorer((level,  path,  file)  ->  path.endsWith(".java"),  (level,  path, 
				file) -> {
					System.out.println(path);
				}).explore(projectDir);
	}
 
}
