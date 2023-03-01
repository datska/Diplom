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

public class TestCard {
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
        //Успешная покупка тура картой со статусом APPROVED
    void shouldBuyByApprovedCard() {
        HomePage homePage = new HomePage();
        BuyByCard buyByCard = homePage.getPageByCard();
        buyByCard.enterCardData(getApprovedCardInfo(), getValidMonth(), getValidYear(), getValidOwner(), getValidCvc());
        buyByCard.getSuccessMessage();
        assertEquals(approvedCard.getStatus(), payData().getStatus());
    }

    @Test
        //Покупка тура картой со статусом DECLINED
    void shouldBuyByDeclineCard() {
        var homePage = new HomePage();
        var buyByCard = homePage.getPageByCard();
        buyByCard.enterCardData(getDeclinedCardInfo(), getValidMonth(), getValidYear(), getValidOwner(), getValidCvc());
        buyByCard.getErrorMessage();
        assertEquals(declinedCard.getStatus(), payData().getStatus());
        checkEmptyOrderEntity();
    }


    @Test
        //Покупка тура с невалидным номером карты
    void shouldSendFormWithInvalidCardNumber() {
        HomePage homePage = new HomePage();
        BuyByCard buyByCard = homePage.getPageByCard();
        buyByCard.enterCardData(getInvalidCardNumber(), getValidMonth(), getValidYear(), getValidOwner(), getValidCvc());
        buyByCard.formatError();
        checkEmptyPaymentEntity();
        checkEmptyOrderEntity();
    }

    @Test
       //Отправка формы с пустым полем "Номер карты"
    void shouldSendFormWithoutCardNumber() {
        HomePage homePage = new HomePage();
        BuyByCard buyByCard = homePage.getPageByCard();
        buyByCard.enterCardData(getEmptyCardNumber(), getValidMonth(), getValidYear(), getValidOwner(), getValidCvc());
        buyByCard.formatError();
        checkEmptyPaymentEntity();
        checkEmptyOrderEntity();
    }

    @Test
       //Отправка формы с невалидным месяцем (однозначное числовое значение)
    void shouldSendFormWithInvalidMonth1() {
        HomePage homePage = new HomePage();
        var buyByCard = homePage.getPageByCard();
        buyByCard.enterCardData(getApprovedCardInfo(), getInvalidMonth1(), getValidYear(), getValidOwner(), getValidCvc());
        buyByCard.formatError();
        checkEmptyPaymentEntity();
        checkEmptyOrderEntity();
    }

    @Test
       //Отправка формы с невалидным месяцем (неверно указан срок действия карты)
    void shouldSendFormWithInvalidMonth2() {
        HomePage homePage = new HomePage();
        var buyByCard = homePage.getPageByCard();
        buyByCard.enterCardData(getApprovedCardInfo(), getInvalidMonth2(), getValidYear(), getValidOwner(), getValidCvc());
        buyByCard.invalidError();
        checkEmptyPaymentEntity();
        checkEmptyOrderEntity();
    }


    @Test
       //Отправка формы с пустым полем "Месяц"
    void shouldSendFormWithoutMonth() {
        HomePage homePage = new HomePage();
        var buyByCard = homePage.getPageByCard();
        buyByCard.enterCardData(getApprovedCardInfo(), getEmptyMonth(), getValidYear(), getValidOwner(), getValidCvc());
        buyByCard.formatError();
        checkEmptyPaymentEntity();
        checkEmptyOrderEntity();
    }

    @Test
        //Отправка формы с невалидным годом (однозначное числовое значение)
    void shouldSendFormWithInvalidYearCard1() {
        HomePage homePage = new HomePage();
        BuyByCard buyByCard = homePage.getPageByCard();
        buyByCard.enterCardData(getApprovedCardInfo(), getValidMonth(), getInvalidYear(), getValidOwner(), getValidCvc());
        buyByCard.formatError();
        checkEmptyPaymentEntity();
        checkEmptyOrderEntity();
    }

    @Test
       //Отправка формы с невалидным годом (неверно указан срок действия карты)
    void shouldSendFormWithInvalidYearCard2() {
        HomePage homePage = new HomePage();
        BuyByCard buyByCard = homePage.getPageByCard();
        buyByCard.enterCardData(getApprovedCardInfo(), getValidMonth(), getInvalidLastYear(), getValidOwner(), getValidCvc());
        buyByCard.expiredError();
        checkEmptyPaymentEntity();
        checkEmptyOrderEntity();
    }

    @Test
       //Отправка формы с пустым полем "Год"
    void shouldSendFormWithoutYear() {
        HomePage homePage = new HomePage();
        BuyByCard buyByCard = homePage.getPageByCard();
        buyByCard.enterCardData(getApprovedCardInfo(), getValidMonth(), getEmptyYear(), getValidOwner(), getValidCvc());
        buyByCard.formatError();
        checkEmptyPaymentEntity();
        checkEmptyOrderEntity();
    }


    @Test
       //Отправка формы с невалидными данными владельца (значение набрано кириллицей)
    void shouldSendFormWithOwnerTypedCyrillic() {
        HomePage homePage = new HomePage();
        BuyByCard buyByCard = homePage.getPageByCard();
        buyByCard.enterCardData(getApprovedCardInfo(), getValidMonth(), getValidYear(), getInvalidOwnerCyrillic(),
                getValidCvc());
        buyByCard.formatError();
        checkEmptyPaymentEntity();
        checkEmptyOrderEntity();
    }

    @Test
        //Отправка формы с введенными в поле "Владелец" цифровыми значениями/спец символами
    void shouldSendFormWithOwnerMaths() {
        HomePage homePage = new HomePage();
        BuyByCard buyByCard = homePage.getPageByCard();
        buyByCard.enterCardData(getApprovedCardInfo(), getValidMonth(), getValidYear(), getInvalidOwnerMaths(),
                getValidCvc());
        buyByCard.formatError();
        checkEmptyPaymentEntity();
        checkEmptyOrderEntity();
    }

    @Test
        //Отправка формы с пустым полем "Владелец"
    void shouldSendFormWithOwnerEmpty() {
        HomePage homePage = new HomePage();
        BuyByCard buyByCard = homePage.getPageByCard();
        buyByCard.enterCardData(getApprovedCardInfo(), getValidMonth(), getValidYear(), getEmptyOwner(), getValidCvc());
        buyByCard.emptyError();
        checkEmptyPaymentEntity();
        checkEmptyOrderEntity();
    }

    @Test
       //Отправка формы с невалидным CVC/CCV
    void shouldSendFormWithInvalidCvc() {
        HomePage homePage = new HomePage();
        BuyByCard buyByCard = homePage.getPageByCard();
        buyByCard.enterCardData(getApprovedCardInfo(), getValidMonth(), getValidYear(), getValidOwner(), getInvalidCvc());
        buyByCard.formatError();
        checkEmptyPaymentEntity();
        checkEmptyOrderEntity();
    }

    @Test
       //Отправка формы с пустым полем "CVC/CCV"
    void shouldSendFormWitheEmptyCvc() {
        HomePage homePage = new HomePage();
        BuyByCard buyByCard = homePage.getPageByCard();
        buyByCard.enterCardData(getApprovedCardInfo(), getValidMonth(), getValidYear(), getValidOwner(), getEmptyCvc());
        buyByCard.formatError();
        checkEmptyPaymentEntity();
        checkEmptyOrderEntity();
    }
}
