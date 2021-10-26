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
		mfiles.forEach(file->{
			FileEntity[] fileE=new FileEntity[mfiles.size()];
			for(int i=0;i<mfiles.size();i++) {
				fileE[i]=new FileEntity();
			
				fileE[i].setBlog(blog);
				try {
					StringBuffer data = new StringBuffer();
					String base64 = new String(Base64.encodeBase64(file.getBytes()),"ASCII");
					data.append("data:image/jpeg;base64,");
					data.append(base64);
					fileE[i].setBase64_str(data.toString());
					fileRepository.save(fileE[i]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			    
		});
	}
}
