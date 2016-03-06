package com.vackosar.gitobsoletebranchremover.control;

import com.google.inject.Singleton;
import com.vackosar.gitobsoletebranchremover.boundary.Console;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ConsoleCredentialsProvider extends CredentialsProvider {
    
    @Inject private Console console;

    private Map<String, String> cache = new HashMap<>();

    @Override
    public boolean isInteractive() {
        return true;
    }

    @Override
    public boolean supports(CredentialItem... items) {
        for (CredentialItem i : items) {
            if (! isSupported(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSupported(CredentialItem i) {
        return i instanceof CredentialItem.InformationalMessage
        || i instanceof CredentialItem.YesNoType
        || i instanceof CredentialItem.CharArrayType
        || i instanceof CredentialItem.StringType;
    }

    @Override
    public boolean get(URIish uri, CredentialItem... items)
            throws UnsupportedCredentialItem {
        for (CredentialItem i : items) {
            if (i instanceof CredentialItem.Username) {
                ((CredentialItem.Username) i).setValue(get(i));
            } else if (i instanceof CredentialItem.Password) {
                ((CredentialItem.Password) i).setValue(get(i).toCharArray());
            } else if (i instanceof CredentialItem.StringType) {
                ((CredentialItem.StringType) i).setValue(get(i));
            } else if (i instanceof CredentialItem.CharArrayType) {
                ((CredentialItem.CharArrayType) i).setValue(get(i).toCharArray());
            } else if (i instanceof CredentialItem.YesNoType) {
                ((CredentialItem.YesNoType) i).setValue(Yn.valueOf(get(i)).equals(Yn.y));
            } else if (i instanceof CredentialItem.InformationalMessage) {
                System.out.println(i.getPromptText());
            } else {
                throw new UnsupportedCredentialItem(uri, i.getClass().getName()+ ":" + i.getPromptText()); //$NON-NLS-1$
            }
        }
        return true;
    }

    private String get(CredentialItem i) {
        if (cache.containsKey(i.getPromptText())) {
            return cache.get(i.getPromptText());
        } else {
            String value = prompt(i);
            cache.put(i.getPromptText(), value);
            return value;
        }
    }

    private String prompt(CredentialItem i) {
        if (i.isValueSecure()) {
            return String.valueOf(console.readPassword(i.getPromptText() + ": "));
        } else {
            return String.valueOf(console.readLine(i.getPromptText() + ": "));
        }
    }

    private String prompt(CredentialItem.YesNoType item) {
        return console.readLine(item.getPromptText() + " [y/n]: ");
    }

    private enum Yn {
        y, n;
    }
}
