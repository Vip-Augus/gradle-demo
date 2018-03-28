package gradle.demo.util.convert;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import gradle.demo.model.CourseRecord;
import gradle.demo.model.dto.CourseRecordDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author by JingQ on 2018/3/27
 */
@Service
public class CourseRecordConvert extends ModelMapper{

    /**
     * records 2 DTOs
     * @param records dos
     * @return DTOs
     */
    public List<CourseRecordDTO> records2DTOS(List<CourseRecord> records) {
        if (CollectionUtils.isEmpty(records)) {
            return Lists.newArrayList();
        }
        return this.map(records, new TypeToken<List<CourseRecordDTO>>(){
        }.getType());
    }

}
