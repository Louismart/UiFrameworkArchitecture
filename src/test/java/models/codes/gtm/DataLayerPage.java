package models.codes.gtm;

public interface DataLayerPage {

    /**
     * Returns DataLayer that must be provided for all Pages in CMS project.
     *
     * As of 2020-04-24, minimum requirement for implementation goes as follows:
     * @Getter
     * mandatoryDataLayer = DataLayer.builder()
     *             .pageName(String))
     *             .pageType(DataLayer.PageType)
     *             .build();
     */
    DataLayer getMandatoryDataLayer();

    /**
     * Method that builds expected DataLayer values, might need visit some pages to obtain the expected data.
     *
     * @return expected DataLayer values
     */
    DataLayer buildDataLayer();
}
