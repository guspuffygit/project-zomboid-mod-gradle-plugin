package io.pzstorm.capsid.setup.xml;

import io.pzstorm.capsid.mod.ModProperties;
import java.nio.file.Paths;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import org.gradle.api.Project;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

/** This class represents an {@code XML} configuration for Discord integration. */
public class DiscordIntegration extends XMLDocument {

    public static final DiscordIntegration INTEGRATION = new DiscordIntegration();

    private DiscordIntegration() {
        super("discord", Paths.get(".idea"));
    }

    @Override
    protected Transformer createAndConfigureTransformer() throws TransformerConfigurationException {

        Transformer transformer = super.createAndConfigureTransformer();

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "");

        return transformer;
    }

    @Override
    public DiscordIntegration configure(Project project) {
        // <project version="4">
        Element projectElement = document.createElement("project");

        projectElement.setAttribute("version", "4");
        appendOrReplaceRootElement(projectElement);

        // <component name="DiscordProjectSettings">
        Element component = document.createElement("component");

        component.setAttribute("name", "DiscordProjectSettings");
        projectElement.appendChild(component);

        // <option name="show" value="PROJECT" />
        Element optionShow = document.createElement("option");

        optionShow.setAttribute("name", "show");
        optionShow.setAttribute("value", "PROJECT");
        component.appendChild(optionShow);

        // <option name="nameOverrideEnabled" value="true" />
        Element optionOverrideEnabled = document.createElement("option");

        optionOverrideEnabled.setAttribute("name", "nameOverrideEnabled");
        optionOverrideEnabled.setAttribute("value", "true");
        component.appendChild(optionOverrideEnabled);

        // <option name="nameOverrideText" value="<projectName>" />
        @Nullable String projectName = (String) project.findProperty(ModProperties.MOD_NAME.name);
        Element optionOverrideText = document.createElement("option");

        optionOverrideText.setAttribute("name", "nameOverrideText");
        optionOverrideText.setAttribute("value", projectName != null ? projectName : "PZ Mod");
        component.appendChild(optionOverrideText);

        // <option name="description" value="<projectDescription>" />
        @Nullable
        String projectDesc = (String) project.findProperty(ModProperties.MOD_DESCRIPTION.name);
        Element optionDescription = document.createElement("option");

        optionDescription.setAttribute("name", "description");
        optionDescription.setAttribute(
                "value", projectDesc != null ? projectDesc : "Project Zomboid mod.");
        component.appendChild(optionDescription);

        return (DiscordIntegration) super.configure(project);
    }
}
