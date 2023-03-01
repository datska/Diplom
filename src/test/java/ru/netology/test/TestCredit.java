package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.DbHelper;
import ru.netology.page.BuyByCard;
import ru.netology.page.BuyByCredit;
import ru.netology.page.HomePage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;
import static ru.netology.data.DbHelper.*;

public class TestCredit {
    DataHelper.CardNumber approvedCard = DataHelper.approvedCardInfo();
    DataHelper.CardNumber declinedCard = DataHelper.declinedCardInfo();

    @BeforeEach
    public void cleanTables() {
        DbHelper.cleanData();
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setupClass() {
        open("http://localhost:8080");
    }


    @Test
       //Успешная покупка тура в кредит картой со статусом APPROVED
    void shouldBuyByApprovedCard() {
        HomePage homePage = new HomePage();
        BuyByCredit buyByCredit = homePage.getPageCredit();
        buyByCredit.enterCardData(getApprovedCardInfo(), getValidMonth(), getValidYear(), getValidOwner(), getValidCvc());
        buyByCredit.getSuccessMessage();
        assertEquals(approvedCard.getStatus(), creditData().getStatus());
    }

    @Test
       //Покупка тура в кредит картой со статусом DECLINED
    void shouldBuyByDeclineCard() {
        HomePage homePage = new HomePage();
        BuyByCredit buyByCredit = homePage.getPageCredit();
        buyByCredit.enterCardData(getDeclinedCardInfo(), getValidMonth(), getValidYear(), getValidOwner(), getValidCvc());
        buyByCredit.getErrorMessage();
        assertEquals(declinedCard.getStatus(), creditData().getStatus());
        checkEmptyOrderEntity();
    }


    @Test
        //Покупка тура в кредит с невалидным номером карты
    void shouldSendFormWithInvalidCardNumber() {
        HomePage homePage = new HomePage();
        BuyByCredit buyByCredit = homePage.getPageCredit();
        buyByCredit.enterCardData(getInvalidCardNumber(), getValidMonth(), getValidYear(), getValidOwner(), getValidCvc());
        buyByCredit.formatError();
        checkEmptyCreditEntity();
        checkEmptyOrderEntity();
    }

    @Test
       //Отправка формы с пустым полем "Номер карты", для покупки в кредит
    void shouldSendFormWithoutCardNumber() {
        HomePage homePage = new HomePage();
        BuyByCredit buyByCredit = homePage.getPageCredit();
        buyByCredit.enterCardData(getEmptyCardNumber(), getValidMonth(), getValidYear(), getValidOwner(), getValidCvc());
        buyByCredit.formatError();
        checkEmptyCreditEntity();
        checkEmptyOrderEntity();
    }

    @Test
       //Отправка формы с невалидным месяцем (однозначное числовое значение), для покупки в кредит
    void shouldSendFormWithInvalidMonth1() {
        HomePage homePage = new HomePage();
        BuyByCredit buyByCredit = homePage.getPageCredit();
        buyByCredit.enterCardData(getApprovedCardInfo(), getInvalidMonth1(), getValidYear(), getValidOwner(), getValidCvc());
        buyByCredit.formatError();
        checkEmptyCreditEntity();
        checkEmptyOrderEntity();
    }

    @Test
       //Отправка формы с невалидным месяцем (неверно указан срок действия карты), для покупки в кредит
    void shouldSendFormWithInvalidMonth2() {
        HomePage homePage = new HomePage();
        BuyByCredit buyByCredit = homePage.getPageCredit();
        buyByCredit.enterCardData(getApprovedCardInfo(), getInvalidMonth2(), getValidYear(), getValidOwner(), getValidCvc());
        buyByCredit.invalidError();
        checkEmptyCreditEntity();
        checkEmptyOrderEntity();
    }

    @Test
       //Отправка формы с пустым полем "Месяц", для покупки в кредит
    void shouldSendFormWithoutMonth() {
        HomePage homePage = new HomePage();
        BuyByCredit buyByCredit = homePage.getPageCredit();
        buyByCredit.enterCardData(getApprovedCardInfo(), getEmptyMonth(), getValidYear(), getValidOwner(), getValidCvc());
        buyByCredit.formatError();
        checkEmptyCreditEntity();
        checkEmptyOrderEntity();
    }

    @Test
        //Отправка формы с невалидным годом (однозначное числовое значение), для покупки в кредит
    void shouldSendFormWithInvalidYearCard1() {
        HomePage homePage = new HomePage();
        BuyByCredit buyByCredit = homePage.getPageCredit();
        buyByCredit.enterCardData(getApprovedCardInfo(), getValidMonth(), getInvalidYear(), getValidOwner(), getValidCvc());
        buyByCredit.formatError();
        checkEmptyCreditEntity();
        checkEmptyOrderEntity();
    }

    @Test
       //Отправка формы с невалидным годом (неверно указан срок действия карты), для покупки в кредит
    void shouldSendFormWithInvalidYearCard2() {
        HomePage homePage = new HomePage();
        BuyByCredit buyByCredit = homePage.getPageCredit();
        buyByCredit.enterCardData(getApprovedCardInfo(), getValidMonth(), getInvalidLastYear(), getValidOwner(), getValidCvc());
        buyByCredit.expiredError();
        checkEmptyCreditEntity();
        checkEmptyOrderEntity();
    }

    @Test
        //Отправка формы с пустым полем "Год", для покупки в кредит
    void shouldSendFormWithoutYear() {
        HomePage homePage = new HomePage();
        BuyByCredit buyByCredit = homePage.getPageCredit();
        buyByCredit.enterCardData(getApprovedCardInfo(), getValidMonth(), getEmptyYear(), getValidOwner(), getValidCvc());
        buyByCredit.formatError();
        checkEmptyCreditEntity();
        checkEmptyOrderEntity();
    }

    @Test
        //Отправка формы с невалидными данными владельца (значение набрано кириллицей), для покупки в кредит
    void shouldSendFormWithOwnerTypedCyrillic() {
        HomePage homePage = new HomePage();
        BuyByCredit buyByCredit = homePage.getPageCredit();
        buyByCredit.enterCardData(getApprovedCardInfo(), getValidMonth(), getValidYear(), getInvalidOwnerCyrillic(),
                getValidCvc());
        buyByCredit.formatError();
        checkEmptyCreditEntity();
        checkEmptyOrderEntity();
    }

    @Test
       //Отправка формы с введенными в поле "Владелец" цифровыми значениями/спец символами, для покупки в кредит
    void shouldSendFormWithOwnerMaths() {
        HomePage homePage = new HomePage();
        BuyByCredit buyByCredit = homePage.getPageCredit();
        buyByCredit.enterCardData(getApprovedCardInfo(), getValidMonth(), getValidYear(), getInvalidOwnerMaths(),
                getValidCvc());
        buyByCredit.formatError();
        checkEmptyCreditEntity();
        checkEmptyOrderEntity();
    }

    @Test
       //Отправка формы с пустым полем "Владелец", для покупки в кредит
    void shouldSendFormWithOwnerEmpty() {
        HomePage homePage = new HomePage();
        BuyByCredit buyByCredit = homePage.getPageCredit();
        buyByCredit.enterCardData(getApprovedCardInfo(), getValidMonth(), getValidYear(), getEmptyOwner(), getValidCvc());
        buyByCredit.emptyError();
        checkEmptyCreditEntity();
        checkEmptyOrderEntity();
    }

    @Test
       //Отправка формы с невалидным CVC/CCV, для покупки в кредит
    void shouldSendFormWithInvalidCvc() {
        HomePage homePage = new HomePage();
        BuyByCredit buyByCredit = homePage.getPageCredit();
        buyByCredit.enterCardData(getApprovedCardInfo(), getValidMonth(), getValidYear(), getValidOwner(), getInvalidCvc());
        buyByCredit.formatError();
        checkEmptyCreditEntity();
        checkEmptyOrderEntity();
    }

    @Test
       //Отправка формы с пустым полем "CVC/CCV", для покупки в кредит
    void shouldSendFormWitheEmptyCvc() {
        HomePage homePage = new HomePage();
        BuyByCredit buyByCredit = homePage.getPageCredit();
        buyByCredit.enterCardData(getApprovedCardInfo(), getValidMonth(), getValidYear(), getValidOwner(), getEmptyCvc());
        buyByCredit.formatError();
        checkEmptyCreditEntity();
        checkEmptyOrderEntity();
    }
}
