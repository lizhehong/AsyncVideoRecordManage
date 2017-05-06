package cn.hy.videorecorder.entity.indentity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;

import cn.hy.videorecorder.entity.convert.PasswordConverter;
import lombok.Data;

@Data
@Embeddable
@MappedSuperclass
public class UserIndentity {

	@Column(name="u_name")
	private String name;
	
	@Column(name="u_password")
	@Convert(converter = PasswordConverter.class)
	private String password;	
	
}
