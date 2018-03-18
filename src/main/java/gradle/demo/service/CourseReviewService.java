package gradle.demo.service;

import gradle.demo.base.BaseServiceTemplate;
import gradle.demo.model.CourseReview;

import java.util.List;

/**
 * Created by GJW on 2018/1/7.
 */
public interface CourseReviewService extends BaseServiceTemplate<CourseReview> {
    List<CourseReview> getListByState(int state);

    void updateState(int id, int state);
}
