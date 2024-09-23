package fr.irif.lingeswaran.memorisation.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MyDao {
    @Query("SELECT * FROM JeuxQuestions")
    fun getAllJeuxQuestions(): Flow<List<JeuxQuestions>>
    @Query("SELECT * FROM Questions WHERE idJeux = :idJeux")
    fun getQuestionsForJeux(idJeux: Int): Flow<List<Questions>>
    @Query("SELECT * FROM JeuxQuestions WHERE nom LIKE '%' || :nom || '%' ")
    fun getJeuxQuestions(nom : String) : Flow<List<JeuxQuestions>>
    @Query("SELECT id FROM JeuxQuestions WHERE nom = :nom")
    fun getJeuxQuestionId(nom: String): Int
    @Query("SELECT * FROM Statistiques")
    fun getStatistiques(): Flow<List<Statistiques>>
    @Query("SELECT scoreEnCours FROM JeuxQuestions WHERE id = :jeuxId")
    fun getScoreEnCours(jeuxId: Int): Int
    @Query("SELECT * FROM Questions WHERE idQuestion = :idQuestion")
    fun getQuestion(idQuestion : Int) : Questions
    @Query("SELECT choix FROM Choix WHERE idQuestion = :idQuestion")
    fun getChoix(idQuestion: Int) : Flow<List<String>>
    @Query("SELECT COUNT(*) FROM Choix WHERE idQuestion = :idQuestion")
    suspend fun getNbrChoix(idQuestion: Int) : Int
    @Query("SELECT * FROM Questions WHERE idQuestion = (SELECT currentQuestion FROM JeuxQuestions WHERE id = :idTheme)")
    suspend fun getCurrentQuestion(idTheme : Int) : Questions
    @Query("SELECT idQuestion FROM Questions WHERE question = :question")
    suspend fun getQuestionId(question : String) : Int

    @Insert
    suspend fun insertJeuxQuestions(jeuxQuestions: JeuxQuestions) : Long
    @Insert
    suspend fun insertQuestion(question: Questions) : Long
    @Insert
    suspend fun insertStatistique(statistiques: Statistiques) : Long
    @Insert
    suspend fun insertChoix(choix : Choix) : Long


    @Query("UPDATE Questions SET status = :newStatus WHERE idQuestion = :questionId")
    suspend fun updateQuestionStatus(questionId: Int, newStatus: Int)
    @Query("UPDATE JeuxQuestions SET nom = :nom WHERE id = :id")
    suspend fun modifyTheme(id: Int, nom : String)
    @Query("UPDATE JeuxQuestions SET scoreEnCours = scoreEnCours + 1 WHERE id = :jeuxId")
    fun incrementScoreEnCours(jeuxId: Int)
    @Query("UPDATE JeuxQuestions SET scoreEnCours = 0 WHERE id = :jeuxId")
    fun setScoreEnCours(jeuxId: Int)
    @Query("UPDATE JeuxQuestions SET currentQuestion = :currentQuestionId WHERE id = :idTheme")
    suspend fun updateCurrentQuestion(idTheme : Int, currentQuestionId : Int)
    @Query("UPDATE Questions SET image = :image WHERE idQuestion = :idQuestion")
    suspend fun updateImageStatus(idQuestion: Int, image: Boolean)


    @Query("DELETE FROM JeuxQuestions WHERE id = :id")
    suspend fun deleteTheme(id: Int)
    @Query("DELETE FROM Questions WHERE idQuestion = :id")
    suspend fun deleteQuestion(id: Int)


}
