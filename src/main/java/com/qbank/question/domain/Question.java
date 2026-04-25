package com.qbank.question.domain;

import com.qbank.common.entity.BaseEntity;
import com.qbank.common.util.StringListConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "questions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CareerLevel careerLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Visibility visibility;

    @Column(name = "my_notes")
    private String myNotes;

    @Convert(converter = StringListConverter.class)
    @Column(name = "key_points")
    private List<String> keyPoints = new ArrayList<>();

    @Column(length = 500)
    private String memo;

    @OneToMany(mappedBy = "question")
    private List<QuestionTag> questionTags = new ArrayList<>();

    public static Question create(Long authorId, String title, CareerLevel careerLevel,
                                   Difficulty difficulty, Visibility visibility,
                                   String myNotes, List<String> keyPoints, String memo) {
        Question q = new Question();
        q.authorId = authorId;
        q.title = title;
        q.careerLevel = careerLevel;
        q.difficulty = difficulty != null ? difficulty : Difficulty.NORMAL;
        q.visibility = visibility;
        q.myNotes = myNotes;
        q.keyPoints = keyPoints != null ? keyPoints : new ArrayList<>();
        q.memo = memo;
        return q;
    }

    public void update(String title, CareerLevel careerLevel, Difficulty difficulty,
                       Visibility visibility, String myNotes, List<String> keyPoints, String memo) {
        this.title = title;
        this.careerLevel = careerLevel;
        this.difficulty = difficulty != null ? difficulty : Difficulty.NORMAL;
        this.visibility = visibility;
        this.myNotes = myNotes;
        this.keyPoints = keyPoints != null ? keyPoints : new ArrayList<>();
        this.memo = memo;
    }
}
