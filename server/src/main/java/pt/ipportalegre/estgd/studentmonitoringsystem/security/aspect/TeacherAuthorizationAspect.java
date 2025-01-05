package pt.ipportalegre.estgd.studentmonitoringsystem.security.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.CurricularUnitRequest;
import pt.ipportalegre.estgd.studentmonitoringsystem.security.CustomUserDetails;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.UserService;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TeacherAuthorizationAspect {

    private final UserService userService;

    @Around("@annotation(pt.ipportalegre.estgd.studentmonitoringsystem.security.aspect.TeacherAuthorization)")
    public Object checkTeacherAuthorization(ProceedingJoinPoint joinPoint) throws Throwable {

        Object[] args = joinPoint.getArgs();
        Long curricularUnitId = null;
        Long teacherIdFromRequest = null;

        // Loop through method arguments to find the appropriate ones
        for (Object arg : args) {
            if (arg instanceof Long) {
                curricularUnitId = (Long) arg;
            } else if (arg instanceof CurricularUnitRequest) {
                teacherIdFromRequest = ((CurricularUnitRequest) arg).getTeacherId();
            }
        }

        // If it's a request for a curricular unit (curricularUnitId is provided)
        if (curricularUnitId != null) {
            log.info("Validating teacher for Curricular Unit ID: {}", curricularUnitId);
            validateTeacherForCurricularUnit(curricularUnitId);
        }
        // If it's a request for creating a curricular unit (teacherIdFromRequest is provided)
        else if (teacherIdFromRequest != null) {
            log.info("Validating teacher for Curricular Unit Request with Teacher ID: {}", teacherIdFromRequest);
            validateTeacherForCurricularUnitRequest(teacherIdFromRequest);
        } else {
            // If neither is found, throw an exception
            throw new IllegalArgumentException("No valid curricularUnitId or teacherId found in arguments.");
        }

        // Proceed with the method execution if authorized
        return joinPoint.proceed();
    }

    // Validate if the logged-in user is the teacher for the curricular unit
    private void validateTeacherForCurricularUnit(Long curricularUnitId) {
        boolean isTeacher = userService.isTeacherOfCurricularUnit(curricularUnitId);
        if (!isTeacher) {
            log.error("Access Denied: User is not authorized to access Curricular Unit ID: {}", curricularUnitId);
            throw new AccessDeniedException("You are not authorized to access this curricular unit.");
        }
    }

    // Validate if the logged-in user is the teacher based on the teacherId from the request
    private void validateTeacherForCurricularUnitRequest(Long teacherIdFromRequest) {
        // Get the currently authenticated user from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = currentUserDetails.getId(); // Get the logged-in user's ID

        if (!teacherIdFromRequest.equals(currentUserId)) {
            log.error("Access Denied: User trying to create curricular unit for another teacher. Teacher ID: {}", teacherIdFromRequest);
            throw new AccessDeniedException("You can only create curricular units for yourself.");
        }
    }
}

