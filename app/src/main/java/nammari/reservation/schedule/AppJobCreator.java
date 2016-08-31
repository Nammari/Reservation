package nammari.reservation.schedule;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by nammari on 8/30/16.
 * email : nammariahmad@gmail.com
 * phone : +962798939560
 */

public class AppJobCreator implements JobCreator {



    @Override
    public Job create(String tag) {
        if(RemoveReservationsJob.TAG.equals(tag)){
            return new RemoveReservationsJob();
        }
        return null;
    }
}
