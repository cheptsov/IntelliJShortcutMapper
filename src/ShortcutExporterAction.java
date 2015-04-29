import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.intellij.ide.CopyPasteManagerEx;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.actionSystem.ex.ActionManagerEx;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.application.ex.ApplicationInfoEx;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.ex.KeymapManagerEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ui.TextTransferable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ShortcutExporterAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final Keymap winKeymap = KeymapManagerEx.getInstance().getKeymap(KeymapManagerEx.DEFAULT_IDEA_KEYMAP);
        final Keymap macKeymap = KeymapManagerEx.getInstance().getKeymap(KeymapManagerEx.MAC_OS_X_10_5_PLUS_KEYMAP);
        final ActionManager actionManager = ActionManagerEx.getInstance();
        final Set<String> winActionIds = new HashSet<String>(Arrays.asList(winKeymap.getActionIds()));
        final Set<String> macActionIds = new HashSet<String>(Arrays.asList(macKeymap.getActionIds()));
        final StringBuilder stringBuilder = new StringBuilder().append("{\n").append("\t\"name\": \"").append(ApplicationNamesInfo.getInstance().getFullProductName()).append("\",\n").append("\t\"version\": \"").append(ApplicationInfoEx.getInstanceEx().getMajorVersion()).append("\",\n").append("\t\"default_context\": \"Global Context\",\n").append("\t\"os\": [\"windows\", \"mac\", \"linux\"],\n").append("\t\"contexts\": {\n").append("\t\t\"Global Context\": {\n");
        final Set<String> commonActionIds = new HashSet<String>();
        commonActionIds.addAll(winActionIds);
        commonActionIds.retainAll(macActionIds);
        winActionIds.removeAll(commonActionIds);
        macActionIds.removeAll(commonActionIds);
        boolean started = false;
        for (String actionId : commonActionIds) {
            final AnAction action = actionManager.getAction(actionId);
            final String winShortcuts = StringUtil.join(FluentIterable.from(Arrays.asList(winKeymap.getShortcuts(actionId))).transform(new Function<Shortcut, String>() {
                @Override
                public String apply(Shortcut shortcut) {
                    return shortcut.toString();
                }
            }).filter(new Predicate<String>() {
                @Override
                public boolean apply(String s) {
                    return !s.contains("button");
                }
            }), ",");
            final String macShortcuts = StringUtil.join(FluentIterable.from(Arrays.asList(macKeymap.getShortcuts(actionId))).transform(new Function<Shortcut, String>() {
                @Override
                public String apply(Shortcut shortcut) {
                    return shortcut.toString();
                }
            }).filter(new Predicate<String>() {
                @Override
                public boolean apply(String s) {
                    return !s.contains("button");
                }
            }), ",");
            if (action != null) {
                final String winShortcutFormatted = formatShortcut(winShortcuts);
                final String macShortcutFormatted = formatShortcut(macShortcuts);
                final String actionName = action.getTemplatePresentation().getText().
                        replaceAll("Class\\.\\.\\.", "Navigate to Class").
                        replaceAll("File\\.\\.\\.", "Navigate to File").
                        replaceAll("Symbol\\.\\.\\.", "Navigate to Symbol").
                        replaceAll("Line\\.\\.\\.", "Navigate to Line").
                        replaceAll("^Type Declaration$", "Navigate to Type Declaration").
                        replaceAll("^Declaration$", "Navigate to Declaration").
                        replaceAll("^Super Method", "Navigate to Super Method").
                        replaceAll("^Implementation\\(s\\)$", "Navigate to Implementation(s)").
                        replaceAll("Method\\.\\.\\.", "Extract Method").
                        replaceAll("Constant\\.\\.\\.", "Extract Constant").
                        replaceAll("Variable\\.\\.\\.", "Extract Variable").
                        replaceAll("Functional Parameter\\.\\.\\.", "Extract Functional Parameter").
                        replaceAll("Parameter\\.\\.\\.", "Extract Parameter").
                        replaceAll("Field\\.\\.\\.", "Extract Field").
                        replaceAll("Basic", "Basic Completion").
                        replaceAll("Test", "Navigate to Test").
                        replaceAll("SmartType", "Smart Completion").
                        replaceAll("\\.\\.\\.", "").
                        replaceAll("New Folder", "New...");
                if (!actionName.contains("XPath") && !actionName.contains("Zoom") &&
                        !actionName.contains("Stretch") && !actionName.contains("Run Ant Target") &&
                        !actionName.equals("Tab") && !actionName.equals("Fully Expand Tree Node") &&
                        !actionName.equals("Set NULL") && !actionName.equals("Add") && !actionName.equals("Send EOF") &&
                        !actionName.equals("Up") && !actionName.equals("Down") && !actionName.equals("Configure Current File Analysis") &&
                        !actionName.equals("Left") && !actionName.equals("Right") && !actionName.equals("Project Directory") &&
                        !actionName.equals("Right") && !actionName.equals("Show/Hide Columns") && !actionName.equals("Maintenance") &&
                        !actionName.equals("Generate DDL") && !actionName.equals("Open Console") && !actionName.equals("Recursive") &&
                        !actionName.equals("Execute Groovy Code") && !actionName.equals("Go To Changed File") &&
                        !actionName.equals("Edit Component") && !actionName.equals("Send to Left") &&
                        !actionName.equals("Send to Right") && !actionName.equals("Edit Group") &&
                        !actionName.equals("Module Directory") && !actionName.equals("Grid") &&
                        !actionName.equals("Delete") && !actionName.equals("Browse") && !actionName.equals("Split Line") &&
                        !actionName.equals("Show Source") && !actionName.equals("Refresh") && !actionName.equals("Edit Maximized") &&
                        !actionName.equals("Reload Page") && !actionName.equals("Edit") && !actionName.equals("Generate SQL") &&
                        !actionName.equals("Set DEFAULT") && !actionName.equals("Level up") && !actionName.equals("Tapestry Class") &&
                        !actionName.equals("Actual Size") && !actionName.equals("Apply Current Layout") &&
                        !actionName.equals("Next Parameter") && !actionName.equals("Tag to Documentation Navigation") &&
                        !actionName.equals("Commit Message History") && !actionName.equals("Exclude") &&
                        !actionName.equals("Show Diff Settings Popup") && !actionName.equals("Desktop Directory") &&
                        !actionName.equals("Next Template Variable or Finish In-Place Refactoring") && !actionName.equals("Indent Selection") &&
                        !macShortcutFormatted.equals("Tag to Documentation Navigation") &&
                        !winShortcutFormatted.isEmpty() && !macShortcutFormatted.isEmpty() &&
                        action.getTemplatePresentation().getText().length() > 1) {
                    if (started) {
                        stringBuilder.append(",\n");
                    }
                    stringBuilder.append("\t\t\t\"").append(actionName).append("\": [").append(winShortcutFormatted).append(", ").append(macShortcutFormatted).append("]");
                    started = true;
                }
            }
        }
        stringBuilder.append("\n\t\t}\n").append("\t}\n").append("}\n");

        CopyPasteManagerEx.getInstanceEx().setContents(new TextTransferable(stringBuilder.toString()));

        Messages.showInfoMessage("Shortcuts have been successfully exported to clipboard", "IntelliJShortcutMapper");
    }


    private static String formatShortcut(String shortcut) {
        return shortcut.
                replaceAll("shift ctrl alt", "shift + ctrl + alt").
                replaceAll("shift ctrl", "shift + ctrl").
                replaceAll("shift alt", "shift + alt").
                replaceAll("ctrl alt", "ctrl + alt").
                replaceAll("meta alt", "meta + alt").
                replaceAll("shift meta", "shift + meta").
                replaceAll("ctrl meta", "ctrl + meta").
                replaceAll("shift meta alt", "shift + meta + alt").
                replaceAll("shift", "Shift").
                replaceAll("ctrl", "Ctrl").
                replaceAll("meta", "Command").
                replaceAll("alt", "Alt").
                replaceAll(" ", "").
                replaceAll("\\]\\+\\[", " or ").
                replaceAll("\\],\\[", " or ").
                replaceAll("\\[", "\"").
                replaceAll("\\]", "\"").
                replaceAll("pressed", "+").
                replaceAll("PAGE_UP", "Page Up").
                replaceAll("PAGE_DOWN", "Page Up").
                replaceAll("RIGHT", "Right Arrow").
                replaceAll("LEFT", "Left Arrow").
                replaceAll("DOWN", "Down").
                replaceAll("UP", "Up").
                replaceAll("HOME", "Home").
                replaceAll("BACK_SPACE", "Backspace").
                replaceAll("DELETE", "Delete").
                replaceAll("TAB", "Tab").
                replaceAll("SLASH", "/").
                replaceAll("DIVIDE", "/").
                replaceAll("INSERT", "Insert").
                replaceAll("CLOSE_BRACKET", "[").
                replaceAll("OPEN_BRACKET", "]").
                replaceAll("BACK_QUOTE", "`").
                replaceAll("QUOTE", "'").
                replaceAll("PERIOD", ".").
                replaceAll("SPACE", "Space").
                replaceAll("ENTER", "Enter").
                replaceAll("SUBTRACT", "-").
                replaceAll("ADD", "+").
                replaceAll("MINUS", "-").
                replaceAll("MULTIPLY", "*").
                replaceAll("ESCAPE", "Esc").
                replaceAll("COMMA", ",").
                replaceAll("EQUALS", "=").
                replaceAll("NUMPAD1", "Numpad 1").
                replaceAll("NUMPAD2", "Numpad 2").
                replaceAll("NUMPAD3", "Numpad 3").
                replaceAll("NUMPAD4", "Numpad 4").
                replaceAll("NUMPAD5", "Numpad 5").
                replaceAll("NUMPAD6", "Numpad 6").
                replaceAll("NUMPAD7", "Numpad 7").
                replaceAll("NUMPAD8", "Numpad 8").
                replaceAll("NUMPAD9", "Numpad 9").
                replaceAll("NUMPAD0", "Numpad 0").
                replaceAll("\"\\+", "\"").
                replaceAll("or \\+", "or ");
    }
}
