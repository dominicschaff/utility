package zz.utility

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_knowledge.*
import zz.utility.helpers.fileAsJsonArray
import zz.utility.helpers.longToast
import zz.utility.helpers.randomIndex

data class Question(val question: String, val answerA: String, val answerB: String, val answerC: String, val answerD: String, val answer: String)

fun JsonArray.toQuestions(): Array<Question> = Gson().fromJson(this, object : TypeToken<Array<Question>>() {}.type)

class KnowledgeActivity : AppCompatActivity() {
    lateinit var questions: Array<Question>

    var question: Int = 0

    var correct: Int = 0
    var total: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_knowledge)

        questions = "$HOME/knowledge_questions.json".fileAsJsonArray().toQuestions()

        answer_a.setOnClickListener { doAnswer("A") }
        answer_b.setOnClickListener { doAnswer("B") }
        answer_c.setOnClickListener { doAnswer("C") }
        answer_d.setOnClickListener { doAnswer("D") }

        displayQuestion()
    }

    private fun doAnswer(answer: String) {
        val question = questions[question]

        if (question.answer == answer) {
            correct++
            displayQuestion()
            score.text = "$correct/$total"
        } else longToast("Incorrect Answer")
    }

    private fun displayQuestion() {
        question = questions.randomIndex()
        total++

        questions[question].apply {
            question_view.text = question
            answer_a.text = answerA
            answer_b.text = answerB
            answer_c.text = answerC
            answer_d.text = answerD
        }
    }
}
