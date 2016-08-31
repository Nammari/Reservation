package nammari.reservation;

import android.app.Application;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import nammari.reservation.schedule.AppJobCreator;
import nammari.reservation.schedule.RemoveReservationsJob;

/**
 * Created by nammari on 8/30/16.
 * email : nammariahmad@gmail.com
 * phone : +962798939560
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //set realm default configuration
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .schemaVersion(Constants.REALM_SCHEME_VERSION)
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        //initialize job manager singleton
        JobManager.create(this).addJobCreator(new AppJobCreator());
        //schedule periodic clear reservation task every 10 minutes ( if needed )
        if (JobManager.instance().getAllJobsForTag(RemoveReservationsJob.TAG).isEmpty()) {
            new JobRequest.Builder(RemoveReservationsJob.TAG)
                    .setPeriodic(TimeUnit.MINUTES.toMillis(Constants.PERIODIC_TIME_IN_MINUTES))//every 10 minutes
                    .setPersisted(true)//the job is scheduled after a reboot
                    .build()
                    .schedule();
        }
    }
}
