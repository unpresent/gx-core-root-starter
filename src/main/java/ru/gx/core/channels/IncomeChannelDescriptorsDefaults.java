package ru.gx.core.channels;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class IncomeChannelDescriptorsDefaults extends AbstractChannelDescriptorsDefaults {
    /**
     * Фильтровальщик, который определяет, требуется ли обрабатывать данные.
     */
    @Nullable
    private LoadingFiltrator loadingFiltrator;

    /**
     * Способ обработки события о получении данных
     */
    @NotNull
    IncomeDataProcessType processType;
}
