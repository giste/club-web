package org.giste.club.web.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TraceLogger {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Before("execution(* org.giste.club.web..*.*(..))")
	public void logBefore(JoinPoint joinPoint) {

		if (LOGGER.isTraceEnabled()) {
			StringBuffer message = new StringBuffer();
			Object[] args;
			int i;

			// Get method arguments and construct log info.
			args = joinPoint.getArgs();
			message.append("ENTER ").append(joinPoint.getTarget().getClass().getName());
			message.append(".").append(joinPoint.getSignature().getName()).append("(");
			for (i = 0; i < args.length; i++) {
				if (i > 0) {
					message.append(", ");
				}
				message.append(args[i]);
			}
			message.append(")");

			LOGGER.trace(message.toString());
		}
	}

	@AfterReturning(pointcut = "execution(!void org.giste.club.web..*.*(..))", returning = "ret")
	public void logReturn(JoinPoint joinPoint, Object returnValue) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("EXIT " + joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName()
					+ " = " + returnValue);
		}
	}

	@After("execution(void org.giste.club.web..*.*(..))")
	public void logExit(JoinPoint joinPoint) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("RETURN " + joinPoint.getTarget().getClass().getName() + joinPoint.getSignature().getName());
		}
	}
}
