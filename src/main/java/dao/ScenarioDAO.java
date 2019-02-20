/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:41
 */

package dao;

/**
 * ScenarioDAO
 * Class that implements the Repository Pattern (DAO).
 * Isolates the data layer, providing data from multiple sources (DB, API).
 *
 * In a near future this class can be adapted, in order to receive a default getData() that make API requests and returns local saved data.
 *
 *  TODO: deal with errors properly
 *
 */
public class ScenarioDAO {

    /**
     * Access the DAO to get the correspondent scenario model.
     * This method is async, notifying the Presenter when query is complete.
     *
     */
    public void requestScenarioById(int scenarioId) {
//
//
//        var context = this;
//        experienceService.getExperience()
//                .then(function (experience) {
//            var experienceModel = context._jsonDeserializerHelper.deserializeExperience(experience);
//            EventBus.dispatch(Events.EXPERIENCE_EVENT, context._callback, experienceModel);
//        }).catch(function (error) {
//            EventBus.dispatch(Events.NO_EXPERIENCE_EVENT, context.callback);
//        })
    }
}
