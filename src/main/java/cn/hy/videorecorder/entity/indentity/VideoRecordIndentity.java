package cn.hy.videorecorder.entity.indentity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;


@Embeddable
@MappedSuperclass
@AttributeOverrides({
	@AttributeOverride(column = @Column(name="vr_name"), name = "name"),
	@AttributeOverride(column = @Column(name="vr_password"), name = "password")
})
public class VideoRecordIndentity extends UserIndentity{

	
}
