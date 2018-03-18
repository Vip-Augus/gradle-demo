package gradle.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import gradle.demo.model.College;
import gradle.demo.service.CollegeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 学院信息
 * @author by JingQ on 2018/1/1
 */
@RestController
@RequestMapping(value = "/web/college")
@Api(value = "学院信息", tags = "Controller")
public class CollegeController {

    @Autowired
    private CollegeService collegeServiceImpl;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public void addCollege(@RequestBody College college, HttpServletRequest request) {
        collegeServiceImpl.add(college);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public void updateCollege(@RequestBody College college, HttpServletRequest request) {
        collegeServiceImpl.update(college);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ResponseBody
    public void delete(@RequestParam("id") Integer id, HttpServletRequest request) {
        collegeServiceImpl.deleteById(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public JSONObject get(@RequestParam("id") Integer id, HttpServletRequest request) {
        College college = collegeServiceImpl.getById(id);
        return (JSONObject) JSON.toJSON(college);
    }
}
