package gradle.demo.model.dto;

import lombok.Data;

/**
 * 公告栏查询参数
 *
 * @author by JingQ on 2018/3/25
 */
@Data
public class NoticeQueryParam {

    private Integer pageNo;

    private Integer pageSize;


}
