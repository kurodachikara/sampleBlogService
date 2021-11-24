package jp.kuroda.sampleBlog.service;

import java.io.IOException;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jp.kuroda.sampleBlog.model.Blog;
import jp.kuroda.sampleBlog.model.FileEntity;
import jp.kuroda.sampleBlog.repository.FileRepository;

@Service
public class UploadFileService {
	@Autowired
	private FileRepository fileRepository;
	
	public void saveFile(List<MultipartFile> mfiles,Blog blog) {
		for(MultipartFile file:mfiles) {
			if(!file.isEmpty()) {
				FileEntity fileE=new FileEntity();
				fileE.setBlog(blog);
				try {
					StringBuffer data = new StringBuffer();
					String base64 = new String(Base64.encodeBase64(file.getBytes()),"ASCII");
					data.append("data:image/jpeg;base64,");
					data.append(base64);
					fileE.setBase64_str(data.toString());
					fileRepository.save(fileE);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				FileEntity fileE=new FileEntity();
				fileE.setBlog(blog);
				fileRepository.save(fileE);
			}
		}
	}
	
	public void deleteImage(FileEntity fileE) {
		fileRepository.deleteById(fileE.getId());
	}
}
