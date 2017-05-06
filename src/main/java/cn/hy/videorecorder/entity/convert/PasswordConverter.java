package cn.hy.videorecorder.entity.convert;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

import org.apache.commons.codec.binary.Base64;



@Convert
public class PasswordConverter implements AttributeConverter<String, String>{

	public String convertToDatabaseColumn(String pwd) {
		return Base64.encodeBase64String(pwd.getBytes());
	}

	public String convertToEntityAttribute(String pwd) {
		return new String(Base64.decodeBase64(pwd));
	}
}
