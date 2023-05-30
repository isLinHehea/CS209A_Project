package cse.java2.project.controller;

import static cse.java2.project.DataAnalyze.extractValues;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/API")
public class MyRestController {

    @GetMapping({"/Visualization/QuestionAnswerNumber"})
    public Map<String, Integer> getQuestionNumberByStatus(@RequestParam(value = "status")
    Optional<String> status) throws IOException {
        String filePath = "src/main/resources/static/js/Number_of_Answers/NoAnswerQuestion.js";
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        int hasAnswerValue = 0, noAnswerValue = 0;
        while ((line = reader.readLine()) != null) {
            if (line.contains("let piechartRaw =")) {
                Map<String, Integer> values = extractValues(line);
                if (values.containsKey("Has answer")) {
                    hasAnswerValue = values.get("Has answer");
                }
                if (values.containsKey("No answer")) {
                    noAnswerValue = values.get("No answer");
                }
            }
        }
        if (status.isPresent()) {
            if (status.get().equals("Has")) {
                Map<String, Integer> resutMap = new HashMap<>();
                resutMap.put("Has answer", hasAnswerValue);
                return resutMap;
            } else if (status.get().equals("No")) {
                Map<String, Integer> resutMap = new HashMap<>();
                resutMap.put("No answer", noAnswerValue);
                return resutMap;
            }
        }
        return null;
    }

    @GetMapping({"/Visualization/QuestionAnswerEvaluation"})
    public Map<String, Integer> getQuestionAnswerEvaluationByStatus(@RequestParam(value = "status")
    Optional<String> status) throws IOException {
        String filePath = "src/main/resources/static/js/Number_of_Answers/AVEMAXNumber.js";
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        int maximum = 0, average = 0;
        while ((line = reader.readLine()) != null) {
            if (line.contains("let barchartRaw =")) {
                Map<String, Integer> values = extractValues(line);
                if (values.containsKey("Maximum")) {
                    maximum = values.get("Maximum");
                }
                if (values.containsKey("Average")) {
                    average = values.get("Average");
                }
            }
        }
        if (status.isPresent()) {
            if (status.get().equals("Maximum")) {
                Map<String, Integer> resutMap = new HashMap<>();
                resutMap.put("Maximum", maximum);
                return resutMap;
            } else if (status.get().equals("Average")) {
                Map<String, Integer> resutMap = new HashMap<>();
                resutMap.put("Average", average);
                return resutMap;
            }
        }
        return null;
    }

    @GetMapping({"/Visualization/AnswerNumberDistribution"})
    public Map<String, Integer> getQuestionAnswerNumberDistributionByStatus(
        @RequestParam(value = "status")
        Optional<String> status) throws IOException {
        String filePath = "src/main/resources/static/js/Number_of_Answers/NumberDistribution.js";
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        int first = 0, second = 0, third = 0, fourth = 0, fifth = 0;
        while ((line = reader.readLine()) != null) {
            if (line.contains("let piechartRaw =")) {
                Map<String, Integer> values = extractValues(line);
                if (values.containsKey("= 0")) {
                    first = values.get("= 0");
                }
                if (values.containsKey(">= 1 & <= 3")) {
                    second = values.get(">= 1 & <= 3");
                }
                if (values.containsKey(">= 4 & <= 6")) {
                    third = values.get(">= 4 & <= 6");
                }
                if (values.containsKey(">= 7 & <= 9")) {
                    fourth = values.get(">= 7 & <= 9");
                }
                if (values.containsKey(">= 10")) {
                    fifth = values.get(">= 10");
                }
            }
        }
        if (status.isPresent()) {
            if (status.get().equals("0")) {
                Map<String, Integer> resutMap = new HashMap<>();
                resutMap.put("= 0", first);
                return resutMap;
            } else if (status.get().equals("1_3")) {
                Map<String, Integer> resutMap = new HashMap<>();
                resutMap.put(">= 1 & <= 3", second);
                return resutMap;
            } else if (status.get().equals("4_6")) {
                Map<String, Integer> resutMap = new HashMap<>();
                resutMap.put(">= 4 & <= 6", third);
                return resutMap;
            } else if (status.get().equals("7_9")) {
                Map<String, Integer> resutMap = new HashMap<>();
                resutMap.put(">= 7 & <= 9", fourth);
                return resutMap;
            } else if (status.get().equals("10")) {
                Map<String, Integer> resutMap = new HashMap<>();
                resutMap.put(">= 10", fifth);
                return resutMap;
            }
        }
        return null;
    }
}
