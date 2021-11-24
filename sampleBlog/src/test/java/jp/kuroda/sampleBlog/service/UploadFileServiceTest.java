package jp.kuroda.sampleBlog.service;


import static org.mockito.Mockito.verify;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;

import jp.kuroda.sampleBlog.model.Blog;
import jp.kuroda.sampleBlog.model.FileEntity;
import jp.kuroda.sampleBlog.repository.FileRepository;

@SpringJUnitConfig
public class UploadFileServiceTest {
	@TestConfiguration
	static class Config{
		@Bean
		public UploadFileService uploadFileService() {
			return new UploadFileService();
		}
	}

	@Autowired
	private UploadFileService uploadFileService;
	
	@MockBean
	private FileRepository fileRepository;

	@Test
	public void testSaveFile() throws UnsupportedEncodingException {
		Blog blog=new Blog();
		FileEntity file=new FileEntity();
		file.setBlog(blog);
		List<MultipartFile> files=new ArrayList<>();
		files.add(new MockMultipartFile("file", "test.text","text/plain","Spring Framework".getBytes()));
		StringBuffer data = new StringBuffer();
        String base64 = new String(Base64.encodeBase64("Spring Framework".getBytes()),"ASCII");
        data.append("data:image/jpeg;base64,");
        data.append(base64);
		file.setBase64_str(data.toString());
		uploadFileService.saveFile(files,blog);
		verify(fileRepository).save(file);
	}
	@Test
	public void testDeleteImage() {
		FileEntity fileEntity=new FileEntity();
		uploadFileService.deleteImage(fileEntity);
		verify(fileRepository).deleteById(fileEntity.getId());
	}
}
