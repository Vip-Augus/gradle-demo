package gradle.demo.model.enums;

import gradle.demo.util.PeriodUtil;

/**
 * 签到时间
 *
 * @author by JingQ on 2018/3/14
 */
public enum SignTime {

    /**
     * 每节课对应的上课时间
     */
    ONE(1, "7:50", "8:20", "第一节课"),
    TWO(2, "8:30", "9:00", "第二节课"),
    THREE(3, "9:25", "9:55", "第三节课"),
    FOUR(4, "10:25", "10:55", "第四节课"),
    FIVE(5, "11:15", "11:45", "第五节课"),
    SIX(6, "13:30", "14:15", "第六节课"),
    SEVEN(7, "13:55", "14:25", "第七节课"),
    EIGHT(8, "14:45", "15:15", "第八节课"),
    NINE(9, "15:45", "16:15", "第九节课"),
    TEN(10, "18:30", "19:15", "第十节课"),
    ELEVEN(11, "19:00", "19:30", "第十一节课"),
    TWELVE(12, "19:50", "20:20", "第十二节课"),
    OTHER(13, "00:00", "01:00", "休息的课");


    private Integer code;
    private String signBeginTime;
    private String signEndTime;
    private String description;

    SignTime(Integer code, String signBeginTime, String signEndTime, String description) {
        this.code = code;
        this.signBeginTime = signBeginTime;
        this.signEndTime = signEndTime;
        this.description = description;
    }

    public static SignTime fromCode(Integer code) {
        for (SignTime time : values()) {
            if (time.getCode().equals(code)) {
                return time;
            }
        }
        return OTHER;
    }

    /**
     * 根据当前时间判断在上那一节课
     *
     * @param currentTime 当前时间
     * @return 课时
     */
    public static SignTime fromTime(String currentTime) {
        if (!PeriodUtil.checkClassTimeFormat(currentTime)) {
            return null;
        }
        for (SignTime time : values()) {
            if (time.getSignBeginTime().compareTo(currentTime) <= 0
                    && time.getSignEndTime().compareTo(currentTime) >= 0) {
                return time;
            }
        }
        return OTHER;
    }

    public Integer getCode() {
        return code;
    }

    public String getSignBeginTime() {
        return signBeginTime;
    }

    public String getSignEndTime() {
        return signEndTime;
    }

    public String getDescription() {
        return description;
    }
}
