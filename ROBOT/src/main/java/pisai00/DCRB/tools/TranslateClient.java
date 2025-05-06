package pisai00.DCRB.tools;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.cloud.translate.TranslateException;

import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("null")
public class TranslateClient {

    private static Translate translateService = null;

    static {
        InputStream serviceAccount = TranslateClient.class.getClassLoader().getResourceAsStream("service_account_key.json");

        if (serviceAccount == null) {
            System.err.println("無法從 classpath 中找到 service_account_key.json");
            System.err.println("請確認 service_account_key.json 是否位於專案的 resources 資料夾下。");
            translateService = null;
        }


        try {
            TranslateOptions translateOptions = TranslateOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
            translateService = translateOptions.getService();
        } catch (IOException e) {
            System.err.println("初始化翻譯服務失敗 (載入憑證時發生 IOException)：" + e.getMessage());
            translateService = null;
        } catch (TranslateException e) {
            System.err.println("初始化翻譯服務失敗 (TranslateException)：" + e.getMessage());
            translateService = null;
        } finally {
            try {
                serviceAccount.close();
            } catch (IOException e) {
                System.err.println("關閉 InputStream 時發生錯誤：" + e.getMessage());
            }
        }

        if (translateService != null) {
        } else {
            System.err.println("翻譯服務初始化失敗。請檢查之前的錯誤訊息。");
        }
    }

    public static String translateText(String text, String lang) {
        if (translateService == null) {
            System.err.println("翻譯服務未初始化，無法進行翻譯。");
            return null;
        }

        try {
            Translation translation = translateService.translate(
                text,
                Translate.TranslateOption.targetLanguage(lang)
            );
            return translation.getTranslatedText();
        } catch (TranslateException e) {
            System.err.println("翻譯文字 '" + text + "' 到 '" + lang + "' 時發生錯誤：" + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("底層原因：" + e.getCause().getMessage());
            }
            return null;
        }
    }
}