 package com.tactuallabs.fmt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateOffsetFileApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			readInCapturesAndGenerateOffsetFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void readInCapturesAndGenerateOffsetFile() throws IOException {
		long[][] totalSignal = new long[30][40];
		
		File dir = new File("./output/");
		
		File[] children = dir.listFiles();

		for(File child:children){
			BufferedReader br = new BufferedReader(new FileReader(child));
			
			String line;
			int row = 0;
			while((line = br.readLine()) != null){
				String[] parts = line.split(",");
				for(int i=0;i<40;i++){
					totalSignal[row][i] += Integer.parseInt(parts[i]);
				}
				row++;
			}
		}
		
		for(int i=0;i<30;i++){
			for(int j=0;j<40;j++){
				totalSignal[i][j] /= children.length;
			}
		}
		
		File out = new File("C:\\TACTUAL\\tactual_offset.txt");
		if(out.exists()){
			out.delete();
		}
		out = new File("C:\\TACTUAL\\tactual_offset.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		
		// why the extra line at the top and bottom?
		for(int j=0;j<40;j++){
			bw.write(totalSignal[0][j]/1000 + "\t");
		}
		bw.write("\n");
		bw.flush();
		
		for(int i=0;i<30;i++){
			for(int j=0;j<40;j++){
				bw.write(totalSignal[i][j] + "\t");
			}
			bw.write("\n");
			bw.flush();
		}
		
		// why the extra line at the top and bottom?
		for(int j=0;j<40;j++){
			bw.write(totalSignal[0][j]/1000 + "\t");
		}
		bw.write("\n");
		bw.flush();
		
		bw.close();
		
	}

}
