package gradle.demo.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 公共文件
 *
 * @author JingQ
 */
@Data
public class PubFile implements Serializable {

    private static final long serialVersionUID = 6746555175807962814L;

    private Integer id;

    private String name;

    private Integer isDirectory;

    private String fileUrl;

    private Integer createId;

    private Date uploadDate;

    private String idNumber;

    private String parentPath;

    private String createDate;

    private String modifyDate;

    /**
     * 课程ID
     */
    private Integer courseId;
}