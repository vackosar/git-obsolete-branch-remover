package com.vackosar.gitiproblemnotifier;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZiper {

    public void act(String zipFile, String outputFolder){
        try{
            ZipInputStream zis = createZipInputStream(zipFile, outputFolder);
            process(outputFolder, zis);
            zis.closeEntry();
            zis.close();
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    private void process(String outputFolder, ZipInputStream zis) throws IOException {
        ZipEntry ze = zis.getNextEntry();
        while(ze!=null){
            String fileName = ze.getName();
            File newFile = new File(outputFolder + File.separator + fileName);
            createParentDirectories(newFile);
            writeToFile(zis, newFile);
            ze = zis.getNextEntry();
        }
    }

    private void writeToFile(ZipInputStream zis, File newFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(newFile);
        int len;
        byte[] buffer = new byte[1024];
        while ((len = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
        }
        fos.close();
    }

    private void createParentDirectories(File newFile) {
        new File(newFile.getParent()).mkdirs();
    }

    private ZipInputStream createZipInputStream(String zipFile, String outputFolder) throws FileNotFoundException {
        createOutputFolder(outputFolder);

        return new ZipInputStream(new FileInputStream(zipFile));
    }

    private void createOutputFolder(String outputFolder) {
        File folder = new File(outputFolder);
        if(!folder.exists()){
            folder.mkdir();
        }
    }
}