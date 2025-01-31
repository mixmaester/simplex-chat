package chat.simplex.app.views.onboarding

import android.Manifest
import android.os.Build
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.simplex.app.R
import chat.simplex.app.SimplexApp
import chat.simplex.app.model.ChatModel
import chat.simplex.app.model.NtfManager
import chat.simplex.app.ui.theme.*
import chat.simplex.app.views.helpers.*
import chat.simplex.app.views.usersettings.NotificationsMode
import chat.simplex.app.views.usersettings.changeNotificationsMode
import com.google.accompanist.permissions.rememberPermissionState
import chat.simplex.res.MR
import dev.icerock.moko.resources.StringResource

@Composable
fun SetNotificationsMode(m: ChatModel) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(vertical = 14.dp)
  ) {
    //CloseSheetBar(null)
    AppBarTitle(stringResource(MR.strings.onboarding_notifications_mode_title))
    val currentMode = rememberSaveable { mutableStateOf(NotificationsMode.default) }
    Column(Modifier.padding(horizontal = DEFAULT_PADDING * 1f)) {
      Text(stringResource(MR.strings.onboarding_notifications_mode_subtitle), Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
      Spacer(Modifier.height(DEFAULT_PADDING * 2f))
      NotificationButton(currentMode, NotificationsMode.OFF, MR.strings.onboarding_notifications_mode_off, MR.strings.onboarding_notifications_mode_off_desc)
      NotificationButton(currentMode, NotificationsMode.PERIODIC, MR.strings.onboarding_notifications_mode_periodic, MR.strings.onboarding_notifications_mode_periodic_desc)
      NotificationButton(currentMode, NotificationsMode.SERVICE, MR.strings.onboarding_notifications_mode_service, MR.strings.onboarding_notifications_mode_service_desc)
    }
    Spacer(Modifier.fillMaxHeight().weight(1f))
    Box(Modifier.fillMaxWidth().padding(bottom = DEFAULT_PADDING_HALF), contentAlignment = Alignment.Center) {
      OnboardingActionButton(MR.strings.use_chat, OnboardingStage.OnboardingComplete, m.onboardingStage, false) {
        changeNotificationsMode(currentMode.value, m)
      }
    }
    Spacer(Modifier.fillMaxHeight().weight(1f))
  }
  SetNotificationsModeAdditions()
}

@Composable
fun SetNotificationsModeAdditions() {
  if (Build.VERSION.SDK_INT >= 33) {
    val notificationsPermissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    LaunchedEffect(notificationsPermissionState.hasPermission) {
      if (notificationsPermissionState.hasPermission) {
        SimplexApp.context.chatModel.controller.ntfManager.createNtfChannelsMaybeShowAlert()
      } else {
        notificationsPermissionState.launchPermissionRequest()
      }
    }
  } else {
    LaunchedEffect(Unit) {
      SimplexApp.context.chatModel.controller.ntfManager.createNtfChannelsMaybeShowAlert()
    }
  }
}

@Composable
private fun NotificationButton(currentMode: MutableState<NotificationsMode>, mode: NotificationsMode, title: StringResource, description: StringResource) {
  TextButton(
    onClick = { currentMode.value = mode },
    border = BorderStroke(1.dp, color = if (currentMode.value == mode) MaterialTheme.colors.primary else MaterialTheme.colors.secondary.copy(alpha = 0.5f)),
    shape = RoundedCornerShape(35.dp),
  ) {
    Column(Modifier.padding(horizontal = 10.dp).padding(top = 4.dp, bottom = 8.dp)) {
      Text(
        stringResource(title),
        style = MaterialTheme.typography.h3,
        fontWeight = FontWeight.Medium,
        color = if (currentMode.value == mode) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
        modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally),
        textAlign = TextAlign.Center
      )
      Text(annotatedStringResource(description),
        Modifier.align(Alignment.CenterHorizontally),
        fontSize = 15.sp,
        color = MaterialTheme.colors.onBackground,
        lineHeight = 24.sp,
        textAlign = TextAlign.Center
      )
    }
  }
  Spacer(Modifier.height(14.dp))
}
