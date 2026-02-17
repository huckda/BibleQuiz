package com.huck.biblequiz.ui.screens.bookselection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huck.biblequiz.model.Book
import com.huck.biblequiz.model.Testament

@Composable
fun BookSelectionScreen(
    onBooksSelected: (List<Int>) -> Unit,
    viewModel: BookSelectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            if (state.selectedBookIds.isNotEmpty()) {
                Button(
                    onClick = { onBooksSelected(state.selectedBookIds.sorted()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Continue (${state.selectedBookIds.size} selected)")
                }
            }
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Failed to load books", style = MaterialTheme.typography.titleLarge)
                        Text(state.error ?: "", style = MaterialTheme.typography.bodyMedium)
                        TextButton(onClick = { viewModel.retry() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            else -> {
                val oldTestament = state.books.filter { it.testament == Testament.OLD }
                val newTestament = state.books.filter { it.testament == Testament.NEW }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),
                    contentPadding = PaddingValues(
                        start = 12.dp, end = 12.dp,
                        top = 12.dp, bottom = 80.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(padding)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            "Select Books",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                        )
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            "Old Testament",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(oldTestament) { book ->
                        BookCard(
                            book = book,
                            isSelected = book.bookId in state.selectedBookIds,
                            onClick = { viewModel.toggleBook(book.bookId) }
                        )
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            "New Testament",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(newTestament) { book ->
                        BookCard(
                            book = book,
                            isSelected = book.bookId in state.selectedBookIds,
                            onClick = { viewModel.toggleBook(book.bookId) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookCard(book: Book, isSelected: Boolean, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline
        )
    ) {
        Text(
            text = book.shortName,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp)
        )
    }
}
