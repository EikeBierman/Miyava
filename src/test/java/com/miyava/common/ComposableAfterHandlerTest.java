package com.miyava.common;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.validation.Errors;

import com.miyava.common.AfterHandler;
import com.miyava.common.ComposableAfterHandler;

@RunWith( MockitoJUnitRunner.class )
public class ComposableAfterHandlerTest {

    @Mock
    private AfterHandler<String> firstHandler;

    @Mock
    private AfterHandler<String> secondHandler;

    @Mock
    private Errors errors;

    private ComposableAfterHandler<String> composite;

    @Before
    public void setup() {
        composite = new ComposableAfterHandler<String>( firstHandler, secondHandler ) {};
        when( errors.hasErrors() ).thenReturn( false );
    }

    @Test
    public void should_execute_multiple_after_handlers_in_row() {
        composite.afterSave( "test", errors );

        verify( firstHandler ).afterSave( anyString(), eq( errors ) );
        verify( secondHandler ).afterSave( anyString(), eq( errors ) );
    }

    @Test
    public void should_not_execute_subsequent_handler_calls_when_errors_occurred() {
        doAnswer( provokeError() ).when( firstHandler ).afterSave( anyString(), eq( errors ) );

        composite.afterSave( "test", errors );

        verify( firstHandler ).afterSave( anyString(), eq( errors ) );
        verify( secondHandler, never() ).afterSave( anyString(), eq( errors ) );
    }

    @Test
    public void should_execute_both_handlers_but_report_errors_afterwards_when_last_handler_registers_errors() {
        doAnswer( provokeError() ).when( secondHandler ).afterSave( anyString(), eq( errors ) );

        assertThat( errors.hasErrors(), is( false ) );

        composite.afterSave( "test", errors );

        verify( firstHandler ).afterSave( anyString(), eq( errors ) );
        verify( secondHandler ).afterSave( anyString(), eq( errors ) );

        assertThat( errors.hasErrors(), is( true ) );
    }

    private Answer<Object> provokeError() {
        return new Answer<Object>() {

            @Override
            public Object answer( InvocationOnMock invocation )
                throws Throwable {
                when( errors.hasErrors() ).thenReturn( true );

                return null;
            }
        };
    }
}
