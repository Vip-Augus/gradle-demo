package gradle.demo.controller;

import gradle.demo.service.FileManageService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author by JingQ on 2018/2/7
 */
@Controller
@Slf4j
@ApiIgnore
public class HomeController {

    @Autowired
    private FileManageService fileManageServiceImpl;

    @RequestMapping("/test")
    public String test(Model model) {
        return "resultPage";
    }

    @ApiOperation(value="上传文件", notes="上传文件", tags = "1.0.0")
    @ApiImplicitParam(name = "file",value = "文件", paramType = "RequestParam")
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ResponseBody
    public String t(@RequestParam("file")MultipartFile file) {
        fileManageServiceImpl.upload(file, "test/");
        return "wahah";
    }

}
