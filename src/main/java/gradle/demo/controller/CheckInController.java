package gradle.demo.controller;

import gradle.demo.model.ExperimentRecord;
import gradle.demo.service.CheckInService;
import gradle.demo.service.ExperimentRecordService;
import gradle.demo.util.result.ApiResponse;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author by JingQ on 2018/3/14
 */
@RestController
@Api(value = "签到管理器", tags = "Controller")
@RequestMapping("/web/checkIn")
public class CheckInController {

    @Autowired
    private CheckInService checkInServiceImpl;

    @Autowired
    private ExperimentRecordService epRecordServiceImpl;

    @RequestMapping("/test")
    public ApiResponse test(@RequestParam("epId") Integer epId, @RequestParam("idNumber") String idNumber) {
        List<ExperimentRecord> records = epRecordServiceImpl.getByEPIdAndUser(epId, idNumber);
        return ApiResponse.success(records);
    }
}
