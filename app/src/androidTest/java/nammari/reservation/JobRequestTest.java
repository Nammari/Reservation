package nammari.reservation;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.JobApi;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by nammari on 8/30/16.
 * email : nammariahmad@gmail.com
 * phone : +962798939560
 */

@RunWith(AndroidJUnit4.class)
public class JobRequestTest {

    @BeforeClass
    public static void beforeClass() {


        JobManager.create(InstrumentationRegistry.getContext()).addJobCreator(new JobCreator() {
            @Override
            public Job create(String tag) {
                return new TestJob();
            }
        });
    }

    @Test
    public void testScheduleAndCancel() {
        JobApi defaultApi = JobApi.getDefault(InstrumentationRegistry.getContext());
        assertThat(getManager().getApi()).isEqualTo(defaultApi);

        JobRequest request = getPeriodicJobRequest();
        int id = request.schedule();

        assertThat(getManager().getJobRequest(id)).isNotNull();
        assertThat(getManager().getJob(id)).isNull();


        boolean canceled = getManager().cancel(id);
        assertThat(canceled).isTrue();

        int cancelCount = getManager().cancelAll();
        assertThat(cancelCount).isEqualTo(1);

        assertThat(getManager().getAllJobRequests()).isEmpty();
        assertThat(getManager().getAllJobs()).isEmpty();
    }


    @After
    public void tearDown() {
        getManager().cancelAll();
    }

    private JobManager getManager() {
        return JobManager.instance();
    }

    private JobRequest getPeriodicJobRequest() {
        return getBuilder()
                .setPeriodic(TimeUnit.MINUTES.toMillis(10))
                .setPersisted(true)
                .build();
    }

    private JobRequest.Builder getBuilder() {
        return new JobRequest.Builder(TestJob.TAG);
    }


    private static final class TestJob extends Job {

        private static final String TAG = "tag";

        @NonNull
        @Override
        protected Result onRunJob(@NonNull Params params) {
            return Result.FAILURE;
        }
    }

}
