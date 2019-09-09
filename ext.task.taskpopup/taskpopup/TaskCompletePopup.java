package ext.task.taskpopup;


/**
 * @formatter:off
 * Last Changed    ::                      $Date: 2017-08-02 10:17:00 +0530 (Wed, 02 Aug 2017) $:
 * Last Changed By ::                      $Author: $:
 * Last Changed Rev::                      $Rev: 235 $:
 * Latest Head URL ::                      $HeadURL: http://mc0wbaac:90/svn/GPDM/trunk/eclipse_config/Eclipse_codetemplates.xml $:
 * @formatter:on
**/

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.DisplayOperationIdentifier;

import wt.fc.collections.WTCollection;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTProperties;

import ext.generic.logger.WCLogger;

/**
 * @author 40007080
 *
 */
public class TaskCompletePopup {

    private static final WCLogger LOGGER = new WCLogger(TaskCompletePopup.class.getName());
    private final String ClassificationFileName = "ClassificationVsEUCommodity.xlsx";
    private final String CLASSIFICATION = "CLASSIFICATION";
    private final String euCommodityCode = "EUCommodityCode";
    private String confirmMessage = null;

    private Map<String, String> validEuCodeMap = new HashMap<>();
    private String validValue = StringUtils.EMPTY;
    private int partCount = 0;
    private char euCodeCount = 'a';

    /**
     * @param NmCommandBean
     * @param List<ObjectBean>
     * @exception WTException
     */

    public String getValidEUcommodityCodes(WTCollection parts) throws Exception {

        LOGGER.enter(" EUCommodityCodeHelper parts : " + parts);

        WTProperties wtProperties = WTProperties.getLocalProperties();
        String wthome = wtProperties.getProperty("wt.home");
        File classficationFile = new File(wthome + File.separator + ClassificationFileName);

        FileInputStream file = new FileInputStream(classficationFile);
        @SuppressWarnings("resource")
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(0);
        Cell cell = null;
        Cell internalName = null;
        Cell displayName = null;
        String cellValue = null;

        @SuppressWarnings("unchecked")
		Iterator<WTCollection> itr = parts.persistableIterator();
        while (itr.hasNext()) {

            validEuCodeMap.clear();
            WTPart part = (WTPart) itr.next();

            euCodeCount = 'a';
            PersistableAdapter persistableAdapter = new PersistableAdapter(part, null, Locale.getDefault(),
                    new DisplayOperationIdentifier());

            persistableAdapter.load(CLASSIFICATION, euCommodityCode);
            String classification = (String) persistableAdapter.get(CLASSIFICATION);
            String euCommodityCodeValue = (String) persistableAdapter.get(euCommodityCode);
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                row = sheet.getRow(rowIndex);
                cell = row.getCell(0);
                cellValue = cell.getStringCellValue();
                if (cellValue.equals(classification)) {//subAttList
                    LOGGER.trace(" Classification cellValue %s" + cellValue);
                    internalName = row.getCell(2); // Internal Name of EU COMMODITY.
                    displayName = row.getCell(3); // Display Name of EU COMMODITY.
                    validEuCodeMap.put(internalName.getStringCellValue(), displayName.getStringCellValue());
                }
            }
            if (validEuCodeMap.get(euCommodityCodeValue) == null) {
                if (confirmMessage != null) {
                    confirmMessage += "\\n" + ++partCount + "> Part - " + part.getNumber() + " :";
                } else {
                    confirmMessage = "\\n" + ++partCount + "> Part - " + part.getNumber() + " :";
                }
                for (String eucode : validEuCodeMap.keySet()) {
                    validValue = eucode + " - " + validEuCodeMap.get(eucode);
                    confirmMessage += "\\n" + euCodeCount++ + ". " + validValue;
                }
            }
        }
        //LOGGER.exit("ConfirmMessage", confirmMessage);
        System.out.println("confirmMessage : " + confirmMessage);
        if (confirmMessage != null) {
            confirmMessage = "EU COMMODITY CODE WARNING!!\\nValid EU Commodity Code Values :" + confirmMessage;
        }
        return confirmMessage;
    }

}
