package com.arabbank.tess4j.ocr;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;


import com.arabbank.tess4j.ocr.model.Status;
import com.arabbank.tess4j.ocr.model.Text;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@SpringBootApplication
@RestController
public class OcrTess4jApplication {

	private Logger logger = LoggerFactory.getLogger(OcrTess4jApplication.class);

	@RequestMapping(value = "ocr/ping", method = RequestMethod.GET)
	public Status ping() throws Exception {
		return new Status("OK");
	}

	@RequestMapping(value = "ocr/v1/convert", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Text convertImageToText(@RequestParam("file") MultipartFile file) throws Exception {

		String fileContent = "";
		BufferedImage bufferedImage = null;

		try {		

			bufferedImage = ImageIO.read(file.getInputStream());
			Tesseract tesseract = new Tesseract();
			//tesseract.setDatapath("C:\\Tessearact\\tessdata\\tessdata-owp\\tessdata\\");
			fileContent = tesseract.doOCR(bufferedImage);
			fileContent = fileContent.replaceAll("\n", " ");
			logger.info(" fileContent after OCR:" + fileContent);
			

		} catch (Exception e) {
			logger.error("Exception while converting/uploading image: ", e);
			throw new TesseractException();
		} finally {
			// tmpFile.delete();
		}

		return new Text(fileContent);
	}

	public static void main(String[] args) {
		SpringApplication.run(OcrTess4jApplication.class, args);
	}

}
