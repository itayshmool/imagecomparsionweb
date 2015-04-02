package service;

import com.google.appengine.api.datastore.*;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: itays
 * Date: 4/24/13
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class DbCleanupUtils {

    public static int deleteAllByType(DatastoreService datastore, String type) {

        Query query;

        if (type.equalsIgnoreCase("compare"))
            query = new Query("CompareEntry");
        else
            query = new Query("UrlCaptureEntry");


        int deleted = deleteQuery(datastore, query);

        return deleted;
    }

    public static int deleteAll(DatastoreService datastore) {

        Query query1;
        Query query2;


        query1 = new Query("CompareEntry");
        query2 = new Query("UrlCaptureEntry");


        int deleted1 = deleteQuery(datastore, query1);
        int deleted2 = deleteQuery(datastore, query2);

        return deleted1 + deleted2;
    }


    public static int deleteByGuid(DatastoreService datastore,String guid) {

        Query query1;
        Query query2;


        query1 = new Query("CompareEntry");
        query2 = new Query("UrlCaptureEntry");

        Query.Filter issueFilter =
                new Query.FilterPredicate("Guid",
                        Query.FilterOperator.EQUAL,
                        guid);


        query1.setFilter(issueFilter);
        query2.setFilter(issueFilter);


        int deleted1 = deleteQuery(datastore, query1);
        if (deleted1 > 0)
            return deleted1;


        int deleted2 = deleteQuery(datastore, query2);
        if (deleted2 > 0)
            return deleted2;


        return 0;
    }

    public static int deleteQuery(DatastoreService datastoreService, Query query) {
        query.setKeysOnly();

        final long start = System.currentTimeMillis();

        int deleted_count = 0;
        boolean is_finished = false;
        final ArrayList<Key> keys = new ArrayList<Key>();

        for (final Entity entity : datastoreService.prepare(query).asIterable(FetchOptions.Builder.withLimit(1000))) {
            keys.add(entity.getKey());
        }

        keys.trimToSize();

        if (keys.size() == 0) {
            is_finished = true;

        }

        while (System.currentTimeMillis() - start < 16384) {

            try {

                datastoreService.delete(keys);

                deleted_count += keys.size();

                break;

            } catch (Throwable ignore) {

                continue;

            }

        }

        return deleted_count;

    }


}
