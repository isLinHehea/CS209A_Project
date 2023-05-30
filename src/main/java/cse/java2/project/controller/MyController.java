package cse.java2.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyController {

    @GetMapping({"/Visualization"})
    public String main() {
        return "Visualize";
    }

    @GetMapping({"/Visualization/QuestionAnswerNumber"})
    public String q1() {
        return "NoAnswerQuestion";
    }

    @GetMapping({"/Visualization/QuestionAnswerEvaluation"})
    public String q2() {
        return "AVEMAXNumber";
    }

    @GetMapping({"/Visualization/AnswerNumberDistribution"})
    public String q3() {
        return "NumberDistribution";
    }

    @GetMapping({"/Visualization/QuestionAcceptedAnswerNumber"})
    public String q4() {
        return "AcceptAnswerQuestion";
    }

    @GetMapping({"/Visualization/QuestionResolutionTimeDistribution"})
    public String q5() {
        return "QuestionResolutionTimeDistribution";
    }

    @GetMapping({"/Visualization/MoreUpvotesNon-acceptedAnswer"})
    public String q6() {
        return "Non-acceptedAnswer";
    }

    @GetMapping({"/Visualization/AppearTogetherWithJavaTag"})
    public String q7() {
        return "AppearTogetherWithJavaTag";
    }

    @GetMapping({"/Visualization/TheMostUpvotesTag"})
    public String q8() {
        return "TheMostUpvotesTag";
    }

    @GetMapping({"/Visualization/TheMostViewsTag"})
    public String q9() {
        return "TheMostViewsTag";
    }

    @GetMapping({"/Visualization/ParticipationDistribution"})
    public String q10() {
        return "ParticipationDistribution";
    }

    @GetMapping({"/Visualization/TheMostActiveUser"})
    public String q11() {
        return "TheMostActiveUser";
    }

    @GetMapping({"/Visualization/FrequentlyDiscussedAPIs"})
    public String q12() {
        return "FrequentlyDiscussedAPIs";
    }
}
