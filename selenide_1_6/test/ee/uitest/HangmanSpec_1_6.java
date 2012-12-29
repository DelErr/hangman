package ee.uitest;

import com.codeborne.selenide.ScreenShooter;
import ee.era.hangman.Launcher;
import ee.era.hangman.model.Word;
import ee.era.hangman.model.Words;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.DOM.*;
import static com.codeborne.selenide.Navigation.baseUrl;
import static com.codeborne.selenide.Navigation.open;
import static ee.era.hangman.di.DependencyInjection.wire;
import static org.junit.Assert.*;

@RunWith(ScreenShooter.class)
public class HangmanSpec_1_6 {
  private static Launcher launcher;

  @BeforeClass
  public static void startServer() throws Exception {
    wire(Words.class, WordsMock.class);
    launcher = new Launcher(8080);
    launcher.run();
    baseUrl = "http://localhost:8080/hangman";
  }

  @AfterClass
  public static void stopServer() {
    launcher.stop();
  }

  @Before
  public void startGame() {
    open("/game");
    $(By.linkText("RUS")).click();
  }

  @Test
  public void showsGameControls() {
    $("#topic").shouldBe(visible);
    $("#wordInWork").shouldBe(visible);
    $("#alphabet").shouldBe(visible);
    $("#hangmanImageContainer").shouldBe(visible);

    $("#topic").should(haveText("дом"));
    $("#wordInWork").should(haveText("______"));
  }

  @Test
  public void guessLetterByClickingLetter() {
    $(By.xpath("//*[@letter='О']")).click();
    waitUntil(By.xpath("//*[@letter='О']"), hasClass("used"));

    $(By.xpath("//*[@letter='Б']")).click();
    waitUntil(By.xpath("//*[@letter='Б']"), hasClass("nonused"));
  }

  @Test
  public void successfulGame() {
    $(By.xpath("//*[@letter='О']")).click();
    $(By.xpath("//*[@letter='З']")).click();
    $(By.xpath("//*[@letter='Д']")).click();
    $(By.xpath("//*[@letter='Г']")).click();
    $(By.xpath("//*[@letter='В']")).click();
    $(By.xpath("//*[@letter='Ь']")).click();
    waitFor(By.id("startGame"));
    $("#gameWin").shouldBe(visible);
    $("#wordInWork").should(haveText("гвоздь"));
  }

  @Test
  public void lostGame() {
    $(By.xpath("//*[@letter='А']")).click();
    $(By.xpath("//*[@letter='Б']")).click();
    $(By.xpath("//*[@letter='В']")).click();
    $(By.xpath("//*[@letter='Г']")).click();
    $(By.xpath("//*[@letter='Д']")).click();
    $(By.xpath("//*[@letter='Е']")).click();
    $(By.xpath("//*[@letter='Ё']")).click();
    $(By.xpath("//*[@letter='Ж']")).click();
    $(By.xpath("//*[@letter='З']")).click();
    $(By.xpath("//*[@letter='И']")).click();
    waitFor(By.id("startGame"));
    $("#gameWin").shouldBe(hidden);
    $("#gameLost").shouldBe(visible);
    $("#wordInWork").should(haveText("гвоздь"));
  }

  @Test
  public void userCanChooseLanguage() {
    $(By.linkText("EST")).click();
    waitUntil(By.id("topic"), hasText("maja"));
    $("#wordInWork").should(haveText("____"));
    assertEquals(27, alphabetLetters().size());

    $(By.linkText("RUS")).click();
    waitUntil(By.id("topic"), hasText("дом"));
    $("#wordInWork").should(haveText("______"));
    assertEquals(33, alphabetLetters().size());

    $(By.linkText("ENG")).click();
    waitUntil(By.id("topic"), hasText("house"));
    $("#wordInWork").should(haveText("____"));
    assertEquals(26, alphabetLetters().size());
  }

  private List<WebElement> alphabetLetters() {
    return $$("#alphabet td");
  }

  public static class WordsMock extends Words {
    @Override
    public Word getRandomWord(String language) {
      if ("ru".equals(language))
        return new Word("дом", "гвоздь");
      if ("et".equals(language))
        return new Word("maja", "nael");
      return new Word("house", "nail");
    }
  }
}