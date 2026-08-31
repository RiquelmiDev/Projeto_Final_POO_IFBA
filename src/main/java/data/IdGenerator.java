package data;

import java.util.concurrent.atomic.AtomicLong;

public final class IdGenerator {
    private static final AtomicLong COUNTER = new AtomicLong(1L);

    private IdGenerator() {
    }

    public static String gerarCodigo(String prefixo) {
        if (prefixo == null || prefixo.isBlank()) {
            throw new IllegalArgumentException("Prefixo obrigatório para geração do identificador.");
        }
        return prefixo + COUNTER.getAndIncrement();
    }

    public static Long gerarSequencial() {
        return COUNTER.getAndIncrement();
    }
}
