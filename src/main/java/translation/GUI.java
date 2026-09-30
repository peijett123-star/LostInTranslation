package translation;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Translator translator = new JSONTranslator();
            CountryCodeConverter countries = new CountryCodeConverter();
            LanguageCodeConverter languages = new LanguageCodeConverter();

            // Only offer countries and languages supported by this translator.
            List<String> countryNames = new ArrayList<>();
            for (String code : translator.getCountryCodes()) {
                countryNames.add(countries.fromCountryCode(code));
            }
            countryNames.sort(String.CASE_INSENSITIVE_ORDER);
            JList<String> countryList = new JList<>(countryNames.toArray(new String[0]));
            countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            countryList.setVisibleRowCount(12);

            JPanel countryPanel = new JPanel(new BorderLayout(0, 5));
            countryPanel.add(new JLabel("Country:"), BorderLayout.NORTH);
            countryPanel.add(new JScrollPane(countryList), BorderLayout.CENTER);

            List<String> languageNames = new ArrayList<>();
            for (String code : translator.getLanguageCodes()) {
                languageNames.add(languages.fromLanguageCode(code));
            }
            languageNames.sort(String.CASE_INSENSITIVE_ORDER);
            JComboBox<String> languageBox = new JComboBox<>(languageNames.toArray(new String[0]));
            JPanel languagePanel = new JPanel(new BorderLayout(5, 0));
            languagePanel.add(new JLabel("Language:"), BorderLayout.WEST);
            languagePanel.add(languageBox, BorderLayout.CENTER);

            JLabel resultLabel = new JLabel("Select a country and language.");
            JPanel resultPanel = new JPanel(new BorderLayout(5, 0));
            resultPanel.add(new JLabel("Translation:"), BorderLayout.WEST);
            resultPanel.add(resultLabel, BorderLayout.CENTER);

            // Display names are converted back to codes before translating.
            Runnable updateTranslation = () -> {
                String country = countryList.getSelectedValue();
                String language = (String) languageBox.getSelectedItem();
                if (country == null || language == null) {
                    resultLabel.setText("Select a country and language.");
                    return;
                }
                String result = translator.translate(
                        countries.fromCountry(country), languages.fromLanguage(language));
                resultLabel.setText(result == null ? "no translation found!" : result);
            };
            countryList.addListSelectionListener(event -> {
                if (!event.getValueIsAdjusting()) {
                    updateTranslation.run();
                }
            });
            languageBox.addActionListener(event -> updateTranslation.run());

            if (!countryNames.isEmpty()) {
                countryList.setSelectedIndex(0);
            }
            updateTranslation.run();

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            mainPanel.add(languagePanel, BorderLayout.NORTH);
            mainPanel.add(countryPanel, BorderLayout.CENTER);
            mainPanel.add(resultPanel, BorderLayout.SOUTH);

            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setMinimumSize(new Dimension(500, 360));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
